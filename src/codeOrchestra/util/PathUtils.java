package codeOrchestra.util;

import codeOrchestra.colt.core.model.Project;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

/**
 * @author Alexander Eliseyev
 */
public class PathUtils {

    private static final String PROJECT_TOKEN = "${project}";
    private static final String COLT_HOME_TOKEN = "${colt_home}";

    private static File applicationBaseDirCached;

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

    public static String makeRelative(String absolutePath, Project project) {
        if (absolutePath == null) {
            return null;
        }
        if ("".equals(absolutePath)) {
            return absolutePath;
        }

        String relativePath = absolutePath;

        String projectPath = project.getBaseDir().getPath();
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
        if (relativePath == null) {
            return null;
        }
        if ("".equals(relativePath)) {
            return relativePath;
        }

        String absolutePath = relativePath;

        for (String replacementToken : replacementTokens) {
            if (absolutePath.contains(replacementToken)) {
                absolutePath = absolutePath.replace(replacementToken, getReplacement(replacementToken));
            }
        }

        absolutePath = FileUtils.normalize(absolutePath);

        return absolutePath;
    }

    public static File getApplicationBaseDir() {
        if (StringUtils.isNotEmpty(System.getProperty("application.home"))) {
            return new File(System.getProperty("application.home"));
        }

        String coltBaseDirProp = System.getProperty("colt.base.dir");

        if (StringUtils.isEmpty(coltBaseDirProp)) {
            File file = new File(PathUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            while (!new File(file, "flex_sdk").exists()) {
              file = file.getParentFile();
            }
            return file;
        } else if (SystemInfo.isMac && "$APP_PACKAGE".equals(coltBaseDirProp)) {
            if (applicationBaseDirCached != null) {
                return applicationBaseDirCached;
            }

            File file = new File(PathUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            while (!file.getName().equals("COLT.app")) {
                file = file.getParentFile();
            }

            applicationBaseDirCached = file;
            return file;
        }

        return new File(coltBaseDirProp);
    }

    public static File getTemplatesDir() {
        return new File(getApplicationBaseDir(), "templates");
    }

    public static File getLibrariesDir() {
        return new File(getApplicationBaseDir(), "lib");
    }

    public static File getExamplesDir() {
        File examplesDir;
        if (SystemInfo.isMac) {
            examplesDir = new File(getApplicationBaseDir().getParentFile(), "projects");
        } else {
            examplesDir = new File(getApplicationBaseDir(), "projects");
        }
        if (examplesDir.exists()) {
            return examplesDir;
        }

        // Looks like we're starting COLT from sources
        return new File(getApplicationBaseDir().getParentFile(), "livecoding_examples");
    }

    public static File getApplicationExecutable() {
        if (SystemInfo.isMac) {
            File executable = new File(getApplicationBaseDir(), "Contents/MacOs/JavaAppLauncher");
            return executable.exists() ? executable : null;
        } else if (SystemInfo.isWindows) {
            File executable = new File(getApplicationBaseDir(), "colt.exe");
            return executable.exists() ? executable : null;
        }

        throw new IllegalStateException("Unsupported OS: " + System.getProperty("os.name"));
    }

    public static boolean checkGradle() {
        String gradleHome = Preferences.userNodeForPackage(PathUtils.class).get("gradle.home2" , "");
        if (StringUtils.isEmpty(gradleHome)) {
            gradleHome = new File(getApplicationBaseDir(), "gradle").getPath();
        }
        return new File(gradleHome).exists();
    }

    public static File getGradleExecutable() {
        String gradleHome = Preferences.userNodeForPackage(PathUtils.class).get("gradle.home", "");
        if (StringUtils.isEmpty(gradleHome)) {
            gradleHome = new File(getApplicationBaseDir(), "gradle").getPath();
        }

        if (SystemInfo.isMac) {
            return new File(gradleHome, "bin/gradle");
        } else if (SystemInfo.isWindows) {
            return new File(gradleHome, "bin/gradle.bat");
        }

        throw new IllegalStateException("Unsupported OS: " + System.getProperty("os.name"));
    }

}
