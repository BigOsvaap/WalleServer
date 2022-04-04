package framework.core;

import framework.WalleServerLoader;
import framework.exception.InvalidConfigurationException;
import framework.http.annotations.Autowired;
import framework.http.annotations.Component;
import framework.http.annotations.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

public final class ApplicationContext {

    private final static Map<Class<?>, Object> beans = new HashMap<>();

    public static <T> T getBean(Class<T> klass) {
        return (T) beans.get(klass);
    }

    public static void init(ClassLoader contextClassLoader, String mainPackage, String path) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        String mainDirectory = mainPackage.replace(".", File.separator);
        List<Class<?>> annotatedClasses = scanForClassesWithAnnotation(mainDirectory, path);

        loadComponents(annotatedClasses);

        List<Class<?>> controllers = annotatedClasses.stream()
                .filter(annotatedClass -> annotatedClass.isAnnotationPresent(RestController.class))
                .toList();
        loadControllers(controllers);

        Properties properties = loadProperties(contextClassLoader);
        Integer port = Integer.parseInt((String) properties.getOrDefault("app.server.port", 1045));
        String contextPath = String.valueOf(properties.getOrDefault("app.server.contextPath", ""));

        WalleServerLoader.loadServer(controllers, port, contextPath);
    }

    private static List<Class<?>> scanForClassesWithAnnotation(String mainDirectory, String path) throws ClassNotFoundException {
        List<Class<?>> classes = new LinkedList<>();
        scanForClassesWithAnnotation(mainDirectory, path, classes);
        if (classes.isEmpty()) {
            throw new InvalidConfigurationException("Invalid Configuration, not a valid directory");
        }
        return classes;
    }

    private static void scanForClassesWithAnnotation(String mainDirectory, String path, List<Class<?>> classes) throws ClassNotFoundException {
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if (Objects.isNull(directoryListing)) {
            return;
        }
        for (File child : directoryListing) {
            if (child.getName().endsWith(".class")) {
                Class<?> klass = getClass(child, mainDirectory);
                boolean isAnnotated = klass.getAnnotations().length != 0;
                if (isAnnotated) {
                    classes.add(klass);
                }
            } else if (child.isDirectory()) {
                scanForClassesWithAnnotation(mainDirectory, child.getAbsolutePath(), classes);
            }
        }
    }

    private static Class<?> getClass(File file, String mainDirectory) throws ClassNotFoundException {
        String absolutePath = file.getAbsolutePath();
        String className = absolutePath.substring(absolutePath.indexOf(mainDirectory))
                .replace(".class", "")
                .replace(File.separator, ".");
        return Class.forName(className);
    }

    private static void loadComponents(List<Class<?>> annotatedClasses) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Class<?>> components = annotatedClasses.stream()
                .filter(annotatedClass -> annotatedClass.isAnnotationPresent(Component.class))
                .toList();
        for (Class<?> component: components) {
            loadComponent(component);
        }
    }

    private static void loadComponent(Class<?> component) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Object object = component.getConstructor().newInstance();
        List<Field> autowiredFields = getAutowiredFields(component);
        boolean hasAutowiredFields = !autowiredFields.isEmpty();
        if (hasAutowiredFields) {
            for (Field field: autowiredFields) {
                Class<?> key = field.getType();
                if (!beans.containsKey(key)) {
                    loadComponent(field.getType());
                }
            }
            injectDependenciesByField(object, autowiredFields);
        }
        beans.put(component, object);
    }

    private static void loadControllers(List<Class<?>> controllers) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (Class<?> controller: controllers) {
            loadController(controller);
        }
    }

    private static void loadController(Class<?> controller) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Object object = controller.getConstructor().newInstance();
        List<Field> autowiredFields = getAutowiredFields(controller);
        boolean hasAutowiredFields = !autowiredFields.isEmpty();
        if (hasAutowiredFields) {
            injectDependenciesByField(object, autowiredFields);
        }
        beans.put(controller, object);
    }

    private static List<Field> getAutowiredFields(Class<?> annotatedClass) {
        return Arrays.stream(annotatedClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .collect(Collectors.toList());
    }

    private static void injectDependenciesByField(Object object, List<Field> autowiredFields) throws IllegalAccessException {
        for (Field autowiredField: autowiredFields) {
            boolean isAccessible = autowiredField.canAccess(object);
            autowiredField.setAccessible(true);
            autowiredField.set(object, beans.get(autowiredField.getType()));
            autowiredField.setAccessible(isAccessible);
        }
    }

    private static Properties loadProperties(ClassLoader contextClassLoader) throws IOException {
        Properties properties = new Properties();
        properties.load(contextClassLoader.getResourceAsStream("application.properties"));
        return properties;
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

}
