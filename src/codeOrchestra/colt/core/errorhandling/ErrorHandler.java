package codeOrchestra.colt.core.errorhandling;

import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.colt.core.ui.ColtApplication;
import codeOrchestra.colt.core.ui.dialog.ColtDialogs;
import javafx.application.Platform;

public class ErrorHandler {

    private static Logger logger = Logger.getLogger(ErrorHandler.class);

    public static void handle(final Throwable t) {
        logger.error(t);

        Runnable runnable = () -> ColtDialogs.showException(ColtApplication.get().getPrimaryStage(), t);

        execInFXThread(runnable);
    }

    public static void handle(final Throwable t, final String message) {
        logger.error(message, t);

        Runnable runnable = () -> ColtDialogs.showException(ColtApplication.get().getPrimaryStage(), t, message);

        execInFXThread(runnable);
    }

    public static void handle(final String message) {
        logger.error(message);
        handle(message, "Error");
    }

    public static void handle(final String message, final String title) {
        logger.error(message);

        Runnable runnable = () -> ColtDialogs.showError(ColtApplication.get().getPrimaryStage(), title, message);

        execInFXThread(runnable);
    }

    private static void execInFXThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }


}
