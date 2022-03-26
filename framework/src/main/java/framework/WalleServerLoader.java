package framework;

import framework.http.annotations.RestController;
import framework.http.server.WalleServer;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class WalleServerLoader {

    public static void loadServer(List<File> classes, String mainPackage)  {
        try {
            WalleServer server = new WalleServer();
            loadRestControllers(server, classes, mainPackage);
        }catch (Exception exception) {

        }
    }

    private static void loadRestControllers(WalleServer server, List<File> classes, String mainPackage) throws ClassNotFoundException {
        String mainDirectory = mainPackage.replace(".", File.separator);
        for (File file: classes) {
            String absolutePath = file.getAbsolutePath();
            String className = absolutePath.substring(absolutePath.indexOf(mainDirectory))
                    .replace(".class", "")
                    .replace(File.separator, ".");
            Class<?> klass = Class.forName(className);
            Optional<RestController> annotation = Optional.of(klass.getAnnotation(RestController.class));
            annotation.ifPresent(restController -> {

            });
        }
    }

}
