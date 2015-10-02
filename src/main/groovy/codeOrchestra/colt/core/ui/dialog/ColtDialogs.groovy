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

    static void showException(Window owner, Throwable exception) {
        showException(owner, exception, null)
    }
    static void showException(Window owner, Throwable exception, String message) {
        ExceptionDialog dialog = new ExceptionDialog(owner)
        dialog.initException(exception, message)
        dialog.show()
    }

    static void showError(Window owner, String title, String massage) {
        showError(owner, title, massage, "");
    }
    static void showError(Window owner, String title, String message, String comment) {
        DialogWithImage dialog = new DialogWithImage(owner)
        dialog.image = new Image("/codeOrchestra/colt/core/ui/style/images/messages/error-48x48.png")
        Dialog.setTitle = title
        dialog.message = message
        dialog.comment = comment
        dialog.show()
    }

    static void showWarning(Window owner, String title, String message) {
        DialogWithImage dialog = new DialogWithImage(owner)
        dialog.image = new Image("/codeOrchestra/colt/core/ui/style/images/messages/warning-48x48.png")
        Dialog.setTitle = title
        dialog.message = message
        dialog.show()
    }

    static void showInfo(Window owner, String title, String message) {
        DialogWithImage dialog = new DialogWithImage(owner)
        dialog.image = new Image("/codeOrchestra/colt/core/ui/style/images/messages/info-48x48.png")
        Dialog.setTitle = title
        dialog.message = message
        dialog.show()
    }
}