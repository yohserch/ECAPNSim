package Components;

import java.io.File;

public class Utils {
    public final static String eca = "eca";
    public final static String pnj = "pnj";
    public final static String mtx = "mtx";
    public final static String obj = "obj";

    // Get the extension of a file

    public static String getExtension(File f) {
        String extension = null;
        String fileName = f.getName();
        int index = fileName.lastIndexOf('.');
        if(index > 0 && index < fileName.length() -1) {
            extension = fileName.substring(index + 1).toLowerCase();
        }
        return extension;
    }
}
