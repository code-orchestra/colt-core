package codeOrchestra.colt.core.ui.components.inputForms

import codeOrchestra.groovyfx.FXBindable
import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser

/**
 * @author Dima Kruk
 */
abstract class InputForm extends AnchorPane implements ITypedForm {
    protected final TextField textField = new TextField(layoutY: 23, prefHeight: 30)
    protected final Button button = new Button(focusTraversable: false, layoutY: 23, prefHeight: 30, prefWidth: 67)

    @FXBindable String title
    @FXBindable String text
    @FXBindable String buttonText = "Browse"
    @FXBindable Boolean buttonDisable
    @FXBindable Boolean textDisable
    @FXBindable Boolean error

    FormType type
    //hak for fxml
    String formType

    InputForm() {
        setLeftAnchor(textField, 10)
        setRightAnchor(textField, 86)
        setRightAnchor(button, 10)

        textField.textProperty().bindBidirectional(text())
        button.textProperty().bindBidirectional(buttonText())

        buttonDisable().addListener({ v, o, newValue ->
            button.disable = newValue
        } as ChangeListener)

        textDisable().addListener({ v, o, newValue ->
            textField.disable = newValue
        } as ChangeListener)

        error().addListener({ v, o, newValue ->
            textField.styleClass.remove("error-input")
            if (newValue) textField.styleClass.addAll("error-input")
        } as ChangeListener)

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

    BrowseType browseType = BrowseType.FILE // todo: переносить в детей

    ArrayList<FileChooser.ExtensionFilter> extensionFilters = new ArrayList<>()// todo: переносить в детей

    boolean numeric

    void setNumeric(boolean numeric) {
        this.numeric = numeric
        if (numeric) {
            textField.textProperty().addListener({ ob, oldValue, String newValue ->
                try {
                    newValue.toInteger()
                } catch (NumberFormatException ignored) {
                    textField.text = oldValue
                }
            } as ChangeListener)
        }
    }

    void setButtonWidth(double value) {
        setRightAnchor(textField, 86 + value - 67)
        button.prefWidth = value
    }

    @Override
    void setType(FormType type) {
        this.type = type
        switch (type) {
            case FormType.SIMPLE:
                children.removeAll(textField, button)
                break
            case FormType.TEXT_FIELD:
                children.remove(button)
                if (!children.contains(textField)) {
                    children.add(textField)
                }
                break
            case FormType.BUTTON:
                if (!children.contains(textField)) {
                    children.add(textField)
                }
                if (!children.contains(button)) {
                    children.add(button)
                }
                break
        }
    }

    void setAction(EventHandler<ActionEvent> action) {
        button.onAction = action
    }
}
