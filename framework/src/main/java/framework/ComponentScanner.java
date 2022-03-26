package framework;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ComponentScanner {

    public static List<File> scanForClasses(String path) {
        List<File> classes = new LinkedList<>();
        scanForClasses(path, classes);
        return classes;
    }

    private static void scanForClasses(String path, List<File> classes) {
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.getName().endsWith(".class")) {
                    classes.add(child);
                } else if (child.isDirectory()) {
                    scanForClasses(child.getAbsolutePath(), classes);
                }
            }
        }
    }

}
