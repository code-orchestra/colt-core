package codeOrchestra.colt.core.ui.components.inputForms.base

import codeOrchestra.colt.core.ui.components.inputForms.markers.MAction
import codeOrchestra.groovyfx.FXBindable
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser

/**
 * @author Dima Kruk
 */
abstract class ActionInputBase extends InputWithErrorBase implements MAction {
    protected final Button button = new Button(focusTraversable: false, layoutY: 23, prefHeight: 30, prefWidth: 67)

    @FXBindable String buttonText = "Browse"
    boolean canBeEmpty

    BrowseType browseType = BrowseType.FILE
    ArrayList<FileChooser.ExtensionFilter> extensionFilters = new ArrayList<>()

    ActionInputBase() {
        button.textProperty().bindBidirectional(buttonText())
        setRightAnchor(button, 10)
        children.add(button)

        action = {
            switch (browseType) {
                case BrowseType.FILE:
                    FileChooser fileChooser = new FileChooser()
                    fileChooser.extensionFilters.addAll(extensionFilters)
                    File file = fileChooser.showOpenDialog(button.scene.window)
                    if (file) {
                        textField.text = file.path
                    }
                    break
                case BrowseType.DIRECTORY:
                    DirectoryChooser directoryChooser = new DirectoryChooser()
                    File file = directoryChooser.showDialog(button.scene.window)
                    if (file) {
                        textField.text = file.path
                    }
                    break
            }
        } as EventHandler<ActionEvent>
    }

    void setButtonWidth(double value) {
        setInputRightAnchor(86 + value - 67)
        button.prefWidth = value
    }

    void setAction(EventHandler<ActionEvent> action) {
        button.onAction = action
    }

    protected boolean validateIsExists() {
        if (textField.disable) {
            error = false
            return error
        }
        if (text) {
            File file = new File(text)
            error = !(file.exists() && (browseType == BrowseType.FILE ? file.isFile() : file.isDirectory()))
        } else {
            error = !canBeEmpty
        }
        return error
    }

    @Override
    boolean validateValue() {
        if (validateIsExists()) {
            activateValidation()
        }
        return error
    }
}
