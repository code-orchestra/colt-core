package codeOrchestra.colt.core.ui.components.inputFormsNew

import codeOrchestra.colt.core.ui.components.inputFormsNew.base.TitledActionInputBase
import javafx.scene.control.Label

/**
 * @author Dima Kruk
 */
class LabeledActionInput extends TitledActionInputBase {

    protected final Label label = new Label()

    LabeledActionInput() {

        setLeftAnchor(label, 19)
        setRightAnchor(label, 48)

        label.textProperty().bindBidirectional(title())

        children.add(label)
    }
}
