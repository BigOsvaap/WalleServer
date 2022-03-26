package framework;

import com.sun.net.httpserver.HttpServer;
import framework.http.annotations.RestController;
import framework.http.handler.GenericHandler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.List;

public class WalleServerLoader {

    public static void loadServer(List<File> classes, String mainPackage)  {
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(9000);
            HttpServer server = HttpServer.create(inetSocketAddress, 0);
            loadRestControllers(server, classes, mainPackage);
            server.setExecutor(null);
            server.start();
        }catch (Exception exception) {

        }
    }

    private static void loadRestControllers(HttpServer server, List<File> classes, String mainPackage) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String mainDirectory = mainPackage.replace(".", File.separator);
        for (File file: classes) {
            String absolutePath = file.getAbsolutePath();
            String className = absolutePath.substring(absolutePath.indexOf(mainDirectory))
                    .replace(".class", "")
                    .replace(File.separator, ".");

            Class<?> klass = Class.forName(className);
            RestController annotation = klass.getAnnotation(RestController.class);
            if (annotation != null) {
                server.createContext(annotation.value(), new GenericHandler<>(klass.getConstructor().newInstance()));
            }
        }
    }

}
