package codeOrchestra.util

import java.awt.*

/**
 * @author Alexander Eliseyev
 */
class ApplicationUtil {

    static boolean startAnotherColtInstance() throws IOException {
        if (SystemInfo.isMac) {
            File baseDir = PathUtils.applicationBaseDir
            if (baseDir.path.endsWith(".app")) {
                Desktop.getDesktop().open(baseDir)
                Runtime.getRuntime().exec("open -n -a " + baseDir.path)
                return true
            }
        } else if (SystemInfo.isWindows) {
            File executable = PathUtils.applicationExecutable
            if (executable != null && executable.exists()) {
                startExecutable(executable.path)
                return true
            }
        } else {
            throw new IllegalStateException("Unsupported OS: " + System.getProperty("os.name"))
        }

        return false
    }

    private static void startExecutable(String executable, String... args) {
        ProcessBuilder builder = new ProcessBuilder(executable)

        if (args.length > 0) {
            builder = builder.command(args)
        }

        builder.start()
    }

}
