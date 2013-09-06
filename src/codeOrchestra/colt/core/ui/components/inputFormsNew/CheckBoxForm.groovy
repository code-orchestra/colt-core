package codeOrchestra.colt.core.ui.components.inputFormsNew


import codeOrchestra.colt.core.ui.components.inputFormsNew.base.TitledForm
import codeOrchestra.groovyfx.FXBindable
import javafx.scene.control.CheckBox

/**
 * @author Dima Kruk
 */
class CheckBoxForm extends TitledForm  {
    @FXBindable boolean selected
    protected final CheckBox checkBox = new CheckBox()

    CheckBoxForm() {
        setLeftAnchor(checkBox, 10)
        setRightAnchor(checkBox, 10)

        checkBox.textProperty().bindBidirectional(title())
        checkBox.selectedProperty().bindBidirectional(selected())

        children.add(checkBox)
    }
}
