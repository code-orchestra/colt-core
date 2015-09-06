package codeOrchestra.colt.core.ui.components.inputForms

import codeOrchestra.colt.core.ui.components.inputForms.base.InputWithErrorBase
import codeOrchestra.colt.core.ui.components.inputForms.markers.MLabeled
import javafx.scene.control.Label

/**
 * @author Dima Kruk
 */
class LabeledTitledInput extends InputWithErrorBase implements MLabeled {
    protected final Label label = new Label()

    LabeledTitledInput() {

        setLeftAnchor(label, 19)
        setRightAnchor(label, 48)

        label.textProperty().bindBidirectional(title())

        children.add(label)
    }
}
