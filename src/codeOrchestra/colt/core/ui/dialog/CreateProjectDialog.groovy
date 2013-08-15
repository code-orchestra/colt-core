package codeOrchestra.colt.core.ui.dialog


import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.Window
import org.controlsfx.control.action.Action
import org.controlsfx.dialog.Dialog

/**
 * @author Dima Kruk
 */
class CreateProjectDialog {

    VBox vBox
    TextField field

    Action okAction

    CreateProjectDialog() {
        vBox = new VBox()
        vBox.padding = new Insets(10, 10, 10, 10)
        vBox.spacing = 5
        Label label = new Label("Enter a project name")
        field = new TextField()
        VBox.setVgrow(field, Priority.ALWAYS)
        Label errorLabel = new Label("Project name can't be empty")
        vBox.children.addAll(label, field, errorLabel)

        errorLabel.visibleProperty().bind(field.textProperty().isEmpty())

        okAction = Dialog.Actions.OK

        okAction.disabledProperty().bind(field.textProperty().isEmpty())
    }

    String show(Window owner) {
        Dialog dlg = new Dialog(owner, "New Live Coding Project", false, true)

        dlg.resizable = false
        dlg.iconifiable = false
        dlg.content = vBox
        dlg.actions.addAll(okAction, Dialog.Actions.CANCEL)

        return dlg.show() == okAction ? field.text : null
    }
}
