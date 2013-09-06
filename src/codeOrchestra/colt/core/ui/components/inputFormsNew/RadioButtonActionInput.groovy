package codeOrchestra.colt.core.ui.components.inputFormsNew

import codeOrchestra.colt.core.ui.components.inputFormsNew.base.ActionInputBase
import codeOrchestra.colt.core.ui.components.inputFormsNew.markers.MSelectable
import codeOrchestra.groovyfx.FXBindable
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.RadioButton

/**
 * @author Dima Kruk
 */
class RadioButtonActionInput extends ActionInputBase implements MSelectable {
    @FXBindable boolean selected

    protected final RadioButton radioButton = new RadioButton()

    RadioButtonActionInput() {
        setLeftAnchor(radioButton, 10)
        setRightAnchor(radioButton, 10)

        radioButton.textProperty().bindBidirectional(title())
        radioButton.selectedProperty().bindBidirectional(selected())

        textField.disableProperty().bind(selected().not())
        button.disableProperty().bind(selected().not())

        children.add(radioButton)
    }

    RadioButton getRadioButton(){
        return radioButton
    }

    @Override
    void activateValidation() {
        super.activateValidation()
        selected().addListener({ ObservableValue<? extends Boolean> observableValue, Boolean t, Boolean newValue ->
            if(newValue) {
                validateValue()
            } else {
                error = false
            }
        } as ChangeListener)
    }
}
