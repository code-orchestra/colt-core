package codeOrchestra.util;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander Eliseyev
 */
public class PathUtils {

    private static final String PROJECT_TOKEN = "${project}";
    private static final String COLT_HOME_TOKEN = "${colt_home}";

    private static Set<String> replacementTokens = new HashSet<String>() {{
        add(PROJECT_TOKEN);
        add(COLT_HOME_TOKEN);
    }};

    private static String getReplacement(String token) {
        if (PROJECT_TOKEN.equals(token)) {
            return ProjectHelper.getCurrentProject().getBaseDir().getPath();
        } else if (COLT_HOME_TOKEN.equals(token)) {
            return getApplicationBaseDir().getPath();
        }
        return null;
    }

    public static String makeRelative(String absolutePath) {
        String relativePath = absolutePath;

        String projectPath = ProjectHelper.getCurrentProject().getBaseDir().getPath();
        if (relativePath.contains(projectPath)) {
            relativePath = absolutePath.replace(projectPath, PROJECT_TOKEN);
        }

        String applicationPath = getApplicationBaseDir().getPath();
        if (relativePath.contains(applicationPath)) {
            relativePath = absolutePath.replace(applicationPath, COLT_HOME_TOKEN);
        }

        return relativePath;
    }

    public static String makeAbsolute(String relativePath) {
        String absolutePath = relativePath;

        for (String replacementToken : replacementTokens) {
            if (absolutePath.contains(replacementToken)) {
                absolutePath = absolutePath.replace(replacementToken, getReplacement(replacementToken));
            }
        }

        return absolutePath;
    }

    public static File getApplicationBaseDir() {
        return new File(System.getProperty("colt.base.dir"));
    }

    public static File getTemplatesDir() {
        return new File(getApplicationBaseDir(), "templates");
    }

}
