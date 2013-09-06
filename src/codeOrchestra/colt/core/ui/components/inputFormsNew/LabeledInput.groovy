package codeOrchestra.colt.core.ui.components.inputFormsNew

import codeOrchestra.colt.core.ui.components.inputFormsNew.base.TitledInputBase
import codeOrchestra.colt.core.ui.components.inputFormsNew.markers.MLabeled
import codeOrchestra.colt.core.ui.components.inputFormsNew.markers.MSimple
import javafx.scene.control.Label

/**
 * @author Dima Kruk
 */
class LabeledInput extends TitledInputBase implements MLabeled, MSimple {
    protected final Label label = new Label()

    LabeledInput() {

        setLeftAnchor(label, 19)
        setRightAnchor(label, 48)

        label.textProperty().bindBidirectional(title())

        children.add(label)
    }
}



