package codeOrchestra.util;

import java.io.File;

/**
 * @author Alexander Eliseyev
 */
public class PathUtils {

    public static File getApplicationBaseDir() {
        return new File(System.getProperty("colt.base.dir"));
    }

}
