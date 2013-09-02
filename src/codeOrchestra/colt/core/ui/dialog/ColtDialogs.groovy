package codeOrchestra.colt.core.ui.dialog

import javafx.event.Event
import javafx.scene.image.Image
import javafx.stage.Window

/**
 * @author Dima Kruk
 */
class ColtDialogs {

    public static void showCloseProjectDialog(Window owner, Event event) {
        SaveDialog dialog = new SaveDialog(owner)
        dialog.show(event)
    }

    static void showException(Window owner, Throwable exception, String massage = null) {
        ExceptionDialog dialog = new ExceptionDialog(owner)
        dialog.initException(exception, massage)

        dialog.show()
    }

    static void showError(Window owner, String title, String massage) {
        DialogWithImage dialog = new DialogWithImage(owner)
        dialog.image = new Image("/codeOrchestra/colt/core/ui/style/images/messages/error-48x48.png")
        dialog.title = title
        dialog.message = massage

        dialog.show()
    }

    static void showWarning(Window owner, String title, String massage) {
        DialogWithImage dialog = new DialogWithImage(owner)
        dialog.image = new Image("/codeOrchestra/colt/core/ui/style/images/messages/warning-48x48.png")
        dialog.title = title
        dialog.message = massage

        dialog.show()
    }

    static void showInfo(Window owner, String title, String massage) {
        DialogWithImage dialog = new DialogWithImage(owner)
        dialog.image = new Image("/codeOrchestra/colt/core/ui/style/images/messages/info-48x48.png")
        dialog.title = title
        dialog.message = massage

        dialog.show()
    }

    static void showApplicationMessage(Window owner, String title, String massage) {
        DialogWithImage dialog = new DialogWithImage(owner)
        dialog.image = new Image("/codeOrchestra/colt/core/ui/style/images/ico-colt.png")
        dialog.title = title
        dialog.message = massage

        dialog.show()
    }
}
