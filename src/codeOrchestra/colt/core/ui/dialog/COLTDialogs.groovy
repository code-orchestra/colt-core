package codeOrchestra.colt.core.ui.dialog

import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Window
import org.controlsfx.control.ButtonBar
import org.controlsfx.control.action.Action
import org.controlsfx.dialog.Dialog

/**
 * @author Dima Kruk
 */
class COLTDialogs {

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

    private static CreateProjectDialog createProjectDialog = new CreateProjectDialog()

    public static String showCreateProjectDialog(Window owner) {
        createProjectDialog.show(owner)
    }

}
