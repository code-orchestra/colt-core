package codeOrchestra.colt.core.ui.components.inputFormsNew

import codeOrchestra.colt.core.ui.components.inputFormsNew.base.ActionInputBase
import codeOrchestra.colt.core.ui.components.inputFormsNew.markers.MLabeled
import javafx.scene.control.Label

/**
 * @author Dima Kruk
 */
class LabeledActionInput extends ActionInputBase implements MLabeled {

    protected final Label label = new Label()

    LabeledActionInput() {

        setLeftAnchor(label, 19)
        setRightAnchor(label, 48)

        label.textProperty().bindBidirectional(title())

        children.add(label)
    }
}
