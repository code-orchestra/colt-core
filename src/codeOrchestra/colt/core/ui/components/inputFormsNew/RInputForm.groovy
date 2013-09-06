package codeOrchestra.colt.core.ui.components.inputFormsNew

import codeOrchestra.colt.core.ui.components.inputFormsNew.base.InputForm
import codeOrchestra.colt.core.ui.components.inputFormsNew.base.SelectableForm
import codeOrchestra.groovyfx.FXBindable
import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField

/**
 * @author Dima Kruk
 */
@Mixin(SelectableForm)
class RInputForm extends InputForm {
    protected final RadioButton radioButton = new RadioButton()

    RInputForm() {
        setLeftAnchor(radioButton, 10)
        setRightAnchor(radioButton, 10)

        radioButton.textProperty().bindBidirectional(title())
        radioButton.selectedProperty().bindBidirectional(selected())

        textField.disableProperty().bind(selected().not())

        children.add(radioButton)

        radioButton.selectedProperty().addListener({ ObservableValue<? extends Boolean> observableValue, Boolean t, Boolean newValue ->
            if(newValue) {
                validateValue()
            } else {
                error = false
            }
        } as ChangeListener)
    }
}
