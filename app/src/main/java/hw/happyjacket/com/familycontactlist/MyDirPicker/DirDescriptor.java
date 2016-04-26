package hw.happyjacket.com.familycontactlist.MyDirPicker;

import java.io.File;

/**
 * Created by jacket on 2016/4/25.
 */
public class DirDescriptor {
    private File dir;

    public DirDescriptor(File f) {
        dir = f;
    }

    public String getDirName() {
        return dir.getName();
    }

    public File getDir() {
        return dir;
    }
}
