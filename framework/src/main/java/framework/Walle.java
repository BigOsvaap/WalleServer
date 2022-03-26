package framework;

import java.io.File;
import java.util.List;

public class Walle {

    public static void run(Class<?> mainClass, String[] args)  {

        String mainPackage = mainClass.getPackageName();
        String path = mainClass.getClassLoader().getResource("").getPath() + mainPackage.replace(".", File.separator);

        List<File> classes = ComponentScanner.scanForClasses(path);

        WalleServerLoader.loadServer(classes, mainPackage);

    }

}
