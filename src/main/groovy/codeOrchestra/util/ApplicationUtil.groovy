package codeOrchestra.util
import codeOrchestra.colt.core.ui.ColtApplication
import javafx.application.Platform

import java.util.prefs.BackingStoreException
import java.util.prefs.Preferences

/**
 * @author Alexander Eliseyev
 */
class ApplicationUtil {

    private static final String LAST_START_REQUEST_KEY = "last_start_request"
    private static final int REQUEST_TIMEOUT = 5000

    private static Preferences preferences = Preferences.userNodeForPackage(ApplicationUtil.class)

    static void startAnotherColtInstance() throws IOException {
        resetLastStartRequestDate()

        if (SystemInfo.isMac) {
            File baseDir = PathUtils.applicationBaseDir
            if (baseDir.path.endsWith(".app")) {
                Runtime.getRuntime().exec("open -n -a " + baseDir.path)
                return
            }
        } else if (SystemInfo.isWindows || SystemInfo.isLinux) {
            File executable = PathUtils.applicationExecutable
            if (executable != null && executable.exists()) {
                startExecutable(executable.path)
                return
            }
        } else {
            throw new IllegalStateException("Unsupported OS: " + System.getProperty("os.name"))
        }

        ApplicationRestarter.start()
    }

    static boolean coltStartWasRecentlyRequested() {
        long lastStartRequestTimestamp = preferences.getLong(LAST_START_REQUEST_KEY, 0)
        return (System.currentTimeMillis() - lastStartRequestTimestamp) < REQUEST_TIMEOUT
    }

    private static void resetLastStartRequestDate() {
        preferences.putLong(LAST_START_REQUEST_KEY, System.currentTimeMillis())
        try {
            preferences.sync()
        } catch (BackingStoreException ignored) {
        }
    }

    static void restartColt() throws IOException {
        startAnotherColtInstance()
        exitColt()
    }

    static void exitColt() {
        ColtApplication.get().dispose()
        Platform.exit()
    }

    private static void startExecutable(String executable, String... args) {
        ProcessBuilder builder = new ProcessBuilder(executable)

        if (args.length > 0) {
            builder = builder.command(args)
        }

        builder.start()
    }

}
