package codeOrchestra.colt.core.ui.components.inputFormsNew

import codeOrchestra.colt.core.ui.components.inputFormsNew.base.SelectableForm
import javafx.scene.control.CheckBox

/**
 * @author Dima Kruk
 */
class CheckBoxForm extends SelectableForm {
    protected final CheckBox checkBox = new CheckBox()

    CheckBoxForm() {
        setLeftAnchor(checkBox, 10)
        setRightAnchor(checkBox, 10)

        checkBox.textProperty().bindBidirectional(title())
        checkBox.selectedProperty().bindBidirectional(selected())

        children.add(checkBox)
    }
}
