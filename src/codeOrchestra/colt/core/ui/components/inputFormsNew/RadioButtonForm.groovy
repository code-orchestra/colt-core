package codeOrchestra.colt.core.ui.components.inputFormsNew


import codeOrchestra.colt.core.ui.components.inputFormsNew.base.TitledForm
import codeOrchestra.groovyfx.FXBindable
import javafx.scene.control.RadioButton

/**
 * @author Dima Kruk
 */
class RadioButtonForm extends TitledForm {
    @FXBindable boolean selected

    protected final RadioButton radioButton = new RadioButton()

    RadioButtonForm() {
        setLeftAnchor(radioButton, 10)
        setRightAnchor(radioButton, 10)

        radioButton.textProperty().bindBidirectional(title())
        radioButton.selectedProperty().bindBidirectional(selected())

        children.add(radioButton)
    }

    RadioButton getRadioButton(){
        return radioButton
    }
}
