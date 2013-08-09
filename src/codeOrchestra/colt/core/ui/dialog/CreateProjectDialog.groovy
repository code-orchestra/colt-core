package codeOrchestra.colt.core.ui.dialog

import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.controlsfx.control.ButtonBar
import org.controlsfx.control.action.AbstractAction
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

    String show(Stage stage) {
        Dialog dlg = new Dialog(stage, "New Live Coding Project")

        dlg.resizable = false
        dlg.content = vBox
        dlg.actions.addAll(Dialog.Actions.CANCEL, okAction)

        return dlg.show() == okAction ? field.text : null
    }
}
