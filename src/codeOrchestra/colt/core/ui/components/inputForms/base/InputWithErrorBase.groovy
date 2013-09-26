package codeOrchestra.colt.core.ui.components.inputForms.base

import codeOrchestra.colt.core.ui.components.inputForms.markers.MInput
import codeOrchestra.groovyfx.FXBindable
import javafx.beans.InvalidationListener
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.scene.control.TextField

/**
 * @author Dima Kruk
 */
abstract class InputWithErrorBase extends TitledInputBase implements MInput {
    protected TextField textField = new TextField(layoutY: 23, prefHeight: 30)

    @FXBindable String text

    @FXBindable Boolean error

    InputWithErrorBase() {
        setLeftAnchor(textField, 10)
        setRightAnchor(textField, 86)

        textField.textProperty().bindBidirectional(text())

        children.add(textField)

        error().addListener({ v, o, newValue ->
            textField.styleClass.remove("error-input")
            if (newValue) textField.styleClass.addAll("error-input")
        } as ChangeListener)
    }

    void setInputRightAnchor(double value) {
        setRightAnchor(textField, value)
    }

    double getInputRightAnchor() {
        return getRightAnchor(textField)
    }

    void setNumeric(boolean numeric) {
        text().addListener({ ob, oldValue, String newValue ->
            try {
                newValue.toInteger()
            } catch (NumberFormatException ignored) {
                text = oldValue
                textField.text = text
            }
        } as ChangeListener)
    }

    protected InvalidationListener validationListener = { javafx.beans.Observable observable ->
        if (!validateValue()) {
            text().removeListener(validationListener)
        }
    } as InvalidationListener

    void activateValidation() {
        text().addListener(validationListener)
    }

    boolean validateValue() {
        if (validateIsNotEmpty()) {
            activateValidation()
        }
        return error
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

    void setBindProperty(StringProperty value) {
        text().bindBidirectional(value)

    }
}
