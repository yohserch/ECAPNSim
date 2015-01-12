package Components;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * Created by serch on 12/01/15.
 */
class ArchFilterEca extends FileFilter{

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);
        if(extension != null) {
            if (extension.equals(Utils.eca)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "ECA files";
    }
}

class ArchFilterPnj extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);
        if (extension != null && extension.equals(Utils.pnj)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "ECAPSim files";
    }
}

class ArchFIlterMtx extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);
        if (extension != null && extension.equals(Utils.mtx)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "Incidence Matrix files";
    }
}

class ArchFilterObj extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);
        if (extension != null && extension.equals(Utils.obj)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "object files";
    }
}
