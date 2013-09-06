package codeOrchestra.colt.core.ui.components.inputFormsNew.base

import codeOrchestra.groovyfx.FXBindable
import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.scene.control.TextField

/**
 * @author Dima Kruk
 */
class InputForm extends TitledForm {
    protected final TextField textField = new TextField(layoutY: 23, prefHeight: 30)

    @FXBindable String text

    @FXBindable Boolean error

    InputForm() {
        setLeftAnchor(textField, 10)
        setRightAnchor(textField, 86)

        textField.textProperty().bindBidirectional(text())

        children.add(textField)

        error().addListener({ v, o, newValue ->
            textField.styleClass.remove("error-input")
            if (newValue) textField.styleClass.addAll("error-input")
        } as ChangeListener)
    }

    void setNumeric(boolean numeric) {
        textField.textProperty().addListener({ ob, oldValue, String newValue ->
            try {
                newValue.toInteger()
            } catch (NumberFormatException ignored) {
                textField.text = oldValue
            }
        } as ChangeListener)
    }

    void activateValidation() {
        text().addListener({ javafx.beans.Observable observable ->
            validateValue()
        } as InvalidationListener)
    }

    boolean validateValue() {
        return validateIsNotEmpty()
    }

    protected boolean validateIsNotEmpty() {
        if (textField.disable) {
            error = false
            return error
        }

        if (text) {
            error = false
        } else {
            error = true
        }
        return error
    }
}
