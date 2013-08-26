package codeOrchestra.colt.core.errorhandling;

import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.colt.core.ui.ColtApplication;
import javafx.application.Platform;
import org.controlsfx.dialog.Dialogs;

public class ErrorHandler {

    private static Logger logger = Logger.getLogger(ErrorHandler.class);

    public static void handle(final Throwable t) {
        logger.error(t);

        Runnable runnable = () -> {
            Dialogs.create()
//                .lightweight()
                    .title("Error")
                    .owner(ColtApplication.get().getPrimaryStage())
                    .nativeTitleBar()
                    .showException(t);
        };

        execInFXThread(runnable);
    }

    public static void handle(final Throwable t, final String message) {
        logger.error(message, t);

        Runnable runnable = () -> {
            Dialogs.create()
//                .lightweight()
                    .title("Error")
                    .owner(ColtApplication.get().getPrimaryStage())
                    .message(message)
                    .nativeTitleBar()
                    .showException(t);
        };

        execInFXThread(runnable);
    }

    public static void handle(final String message) {
        logger.error(message);
        handle(message, "Error");
    }

    public static void handle(final String message, final String title) {
        logger.error(message);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Dialogs.create()
//                .lightweight()
                        .title(title)
                        .owner(ColtApplication.get().getPrimaryStage())
                        .message(message)
                        .nativeTitleBar()
                        .showError();
            }
        };

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
