package codeOrchestra.util

import codeOrchestra.colt.core.ui.ColtApplication
import javafx.application.Platform

import java.awt.*

/**
 * @author Alexander Eliseyev
 */
class ApplicationUtil {

    static void startAnotherColtInstance() throws IOException {
        if (SystemInfo.isMac) {
            File baseDir = PathUtils.applicationBaseDir
            if (baseDir.path.endsWith(".app")) {
                Desktop.getDesktop().open(baseDir)
                Runtime.getRuntime().exec("open -n -a " + baseDir.path)
                return
            }
        } else if (SystemInfo.isWindows) {
            File executable = PathUtils.applicationExecutable
            if (executable != null && executable.exists()) {
                startExecutable(executable.path)
                return
            }
        } else {
            throw new IllegalStateException("Unsupported OS: " + System.getProperty("os.name"))
        }

        ApplicationRestarter.start();
    }

    static void restartColt() throws IOException {
        startAnotherColtInstance()
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
