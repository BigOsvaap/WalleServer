package framework;

import framework.core.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Walle {

    public static void run(Class<?> mainClass, String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {

        String mainPackage = mainClass.getPackageName();
        ClassLoader contextClassLoader = mainClass.getClassLoader();
        String path = contextClassLoader.getResource("").getPath() + mainPackage.replace(".", File.separator);

        ApplicationContext.init(contextClassLoader, mainPackage, path);

    }

}
