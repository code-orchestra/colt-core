package codeOrchestra.colt.core.ui.components.inputForms.base

import codeOrchestra.colt.core.model.Project
import codeOrchestra.colt.core.ui.components.inputForms.markers.MAction
import codeOrchestra.groovyfx.FXBindable
import codeOrchestra.util.PathUtils
import codeOrchestra.util.SystemInfo
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.util.StringConverter

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
                case BrowseType.APPLICATION:
                    FileChooser fileChooser = new FileChooser()
                    fileChooser.extensionFilters.addAll(extensionFilters)
                    File file = fileChooser.showOpenDialog(button.scene.window)
                    if (file) {
                        text = file.path
                    }
                    break
                case BrowseType.DIRECTORY:
                    DirectoryChooser directoryChooser = new DirectoryChooser()
                    File file = directoryChooser.showDialog(button.scene.window)
                    if (file) {
                        text = file.path
                    }
                    break
            }
        } as EventHandler<ActionEvent>
    }

    void setShortPathForProject(Project project) {
        textField.textProperty().unbindBidirectional(text())
        text().addListener({ ObservableValue<? extends String> observableValue, String t, String t1 ->
            println "t1 = $t1"
        } as ChangeListener)
        StringConverter converter = new StringConverter<String>() {
            @Override
            String toString(String t) {
                String path = PathUtils.makeRelative(t, project)
                if (path?.contains("\${project}")) {
                    path = path.replace("\${project}/", "")
                    println "path = $path"
                }
                return path
            }

            @Override
            String fromString(String s) {
                String result = s

                File absolute = new File(PathUtils.makeAbsolute("\${project}/".concat(s)))
                if (absolute.exists()) {
                    result = absolute
                }

                return result
            }
        }
        textField.textProperty().bindBidirectional(text(), converter)
        textField.textProperty().addListener({ ObservableValue<? extends String> observableValue, String t, String newValue ->
            textField.text = converter.toString(newValue)
        } as ChangeListener)
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
            switch (browseType) {
                case BrowseType.FILE:
                    error = !(file.exists() && file.isFile())
                    break
                case BrowseType.DIRECTORY:
                    error = !(file.exists() && file.isDirectory())
                    break
                case BrowseType.APPLICATION:
                    error = !(file.exists() && (SystemInfo.isMac ? file.isDirectory() : file.isFile()))
                    break
            }
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

    boolean validateIsEmpty() {
        if (validateIsNotEmpty()) {
            activateValidation()
        }
        return error
    }
}
