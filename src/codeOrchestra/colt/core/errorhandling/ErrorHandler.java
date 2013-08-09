package codeOrchestra.colt.core.errorhandling;

import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.colt.core.ui.COLTApplication;
import org.controlsfx.dialog.Dialogs;

public class ErrorHandler {

    private static Logger logger = Logger.getLogger(ErrorHandler.class);

    public static void handle(final Throwable t) {
        logger.error(t);
        Dialogs.create()
//                .lightweight()
                .title("Error")
                .owner(COLTApplication.get().getPrimaryStage())
                .nativeChrome()
                .showException(t);
    }

    public static void handle(final Throwable t, final String message) {
        logger.error(message, t);
        Dialogs.create()
//                .lightweight()
                .title("Error")
                .owner(COLTApplication.get().getPrimaryStage())
                .message(message)
                .nativeChrome()
                .showException(t);
    }

    public static void handle(final String message) {
        logger.error(message);
        handle(message, "Error");
    }

    public static void handle(final String message, final String title) {
        logger.error(message);
        Dialogs.create()
//                .lightweight()
                .title(title)
                .owner(COLTApplication.get().getPrimaryStage())
                .message(message)
                .nativeChrome()
                .showError();
    }

}
