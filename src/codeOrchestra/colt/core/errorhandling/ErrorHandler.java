package codeOrchestra.colt.core.errorhandling;

import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.colt.core.ui.ColtApplication;
import codeOrchestra.colt.core.ui.dialog.ColtDialogs;
import javafx.application.Platform;

public class ErrorHandler {

    private static final long ERROR_DIALOG_TIMEOUT = 1500;

    private static Logger logger = Logger.getLogger(ErrorHandler.class);

    private static long lastTimeDialogWasShown;

    public static void handle(final Throwable t) {
        logger.error(t);

        Runnable runnable = () -> ColtDialogs.showException(ColtApplication.get().getPrimaryStage(), t);

        execInFXThreadByTimeout(runnable);
    }

    public static void handle(final Throwable t, final String message) {
        logger.error(message, t);

        Runnable runnable = () -> ColtDialogs.showException(ColtApplication.get().getPrimaryStage(), t, message);

        execInFXThreadByTimeout(runnable);
    }

    public static void handle(final String message) {
        logger.error(message);
        handle(message, "Error");
    }

    public static void handle(final String message, final String title) {
        logger.error(message);

        Runnable runnable = () -> ColtDialogs.showError(ColtApplication.get().getPrimaryStage(), title, message);

        execInFXThreadByTimeout(runnable);
    }

    private static void execInFXThreadByTimeout(Runnable runnable) {
        Runnable theRunnable = () -> {
            if (System.currentTimeMillis() - lastTimeDialogWasShown > ERROR_DIALOG_TIMEOUT) {
                runnable.run();
            }
            lastTimeDialogWasShown = System.currentTimeMillis();
        };

        if (Platform.isFxApplicationThread()) {
            theRunnable.run();
        } else {
            Platform.runLater(theRunnable);
        }
    }


}
