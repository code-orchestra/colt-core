package codeOrchestra.colt.core.ui.components.inputFormsNew


import codeOrchestra.colt.core.ui.components.inputFormsNew.base.TitledInputBase
import codeOrchestra.groovyfx.FXBindable
import javafx.scene.control.CheckBox

/**
 * @author Dima Kruk
 */
class CheckBoxInput extends TitledInputBase  {
    @FXBindable boolean selected
    protected final CheckBox checkBox = new CheckBox()

    CheckBoxInput() {
        setLeftAnchor(checkBox, 10)
        setRightAnchor(checkBox, 10)

        checkBox.textProperty().bindBidirectional(title())
        checkBox.selectedProperty().bindBidirectional(selected())

        children.add(checkBox)
    }
}
