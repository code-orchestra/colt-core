package codeOrchestra.colt.core.ui.components.inputForms

import codeOrchestra.colt.core.ui.components.inputForms.base.TitledInputBase
import codeOrchestra.colt.core.ui.components.inputForms.markers.MSelectable
import codeOrchestra.colt.core.ui.components.inputForms.markers.MSimple
import codeOrchestra.groovyfx.FXBindable
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup

/**
 * @author Dima Kruk
 */
class RadioButtonInput extends TitledInputBase implements MSelectable, MSimple {
    @FXBindable boolean selected

    protected final RadioButton radioButton = new RadioButton()

    RadioButtonInput() {
        setLeftAnchor(radioButton, 10)
        setRightAnchor(radioButton, 10)

        radioButton.textProperty().bindBidirectional(title())
        radioButton.selectedProperty().bindBidirectional(selected())

        children.add(radioButton)
    }

    RadioButton getRadioButton(){
        return radioButton
    }

    void setToggleGroup(ToggleGroup value) {
        radioButton.toggleGroup = value
    }
}
