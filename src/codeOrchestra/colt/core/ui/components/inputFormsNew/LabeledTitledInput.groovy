package codeOrchestra.colt.core.ui.components.inputFormsNew


import codeOrchestra.colt.core.ui.components.inputFormsNew.base.TitledInputWithErrorBase
import javafx.scene.control.Label

/**
 * @author Dima Kruk
 */
class LabeledTitledInput extends TitledInputWithErrorBase {
    protected final Label label = new Label()

    LabeledTitledInput() {

        setLeftAnchor(label, 19)
        setRightAnchor(label, 48)

        label.textProperty().bindBidirectional(title())

        children.add(label)
    }
}
