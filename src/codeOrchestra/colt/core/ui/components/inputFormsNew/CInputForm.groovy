package codeOrchestra.colt.core.ui.components.inputFormsNew

import codeOrchestra.colt.core.ui.components.inputFormsNew.base.InputForm
import codeOrchestra.colt.core.ui.components.inputFormsNew.base.SelectableForm
import codeOrchestra.groovyfx.FXBindable
import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField

/**
 * @author Dima Kruk
 */
@Mixin(SelectableForm)
class CInputForm extends InputForm {
    protected final CheckBox checkBox = new CheckBox()

    CInputForm() {
        setLeftAnchor(checkBox, 10)
        setRightAnchor(checkBox, 10)

        checkBox.textProperty().bindBidirectional(title())
        checkBox.selectedProperty().bindBidirectional(selected())

        children.add(checkBox)

        textField.disableProperty().bind(selected().not())

        checkBox.selectedProperty().addListener({ ObservableValue<? extends Boolean> observableValue, Boolean t, Boolean newValue ->
            if(newValue) {
                validateValue()
            } else {
                error = false
            }
        } as ChangeListener)
    }
}
