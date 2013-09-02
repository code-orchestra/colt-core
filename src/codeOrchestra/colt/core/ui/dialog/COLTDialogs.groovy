package codeOrchestra.colt.core.ui.dialog

import javafx.scene.image.Image
import javafx.stage.Window
import org.controlsfx.control.ButtonBar
import org.controlsfx.control.action.Action
import org.controlsfx.dialog.Dialog

/**
 * @author Dima Kruk
 */
class ColtDialogs {

    public static Action showCloseProjectDialog(Window owner) {
        Action save = Dialog.Actions.YES
        save.textProperty().set("Save")

        Action dontSave = Dialog.Actions.NO
        dontSave.textProperty().set("Don't Save")
        ButtonBar.setType(dontSave, ButtonBar.ButtonType.LEFT)

        Dialog dlg = new Dialog(owner, "Do you want to save the changed you made?", false, true)
        dlg.setContent("Your changes will be lost if you don't save them.")
        dlg.actions.addAll(save, dontSave, Dialog.Actions.CANCEL)
        return dlg.show()
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
