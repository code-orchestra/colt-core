package codeOrchestra.colt.core.ui.components.inputFormsNew

import codeOrchestra.colt.core.ui.components.inputFormsNew.base.TitledForm
import javafx.scene.control.Label

/**
 * @author Dima Kruk
 */
class LabelForm extends TitledForm {
    protected final Label label = new Label()

    LabelForm() {
        children.addAll(label)

        setLeftAnchor(label, 19)
        setRightAnchor(label, 48)

        label.textProperty().bindBidirectional(title())

        children.add(label)
    }
}



