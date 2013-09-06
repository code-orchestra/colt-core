package codeOrchestra.colt.core.ui.components.inputFormsNew

import codeOrchestra.colt.core.ui.components.inputFormsNew.base.ActionForm
import javafx.scene.control.Label

/**
 * @author Dima Kruk
 */
class LActionFrom extends ActionForm {

    protected final Label label = new Label()

    LActionFrom() {
        children.addAll(label)

        setLeftAnchor(label, 19)
        setRightAnchor(label, 48)

        label.textProperty().bindBidirectional(title())

        children.add(label)
    }
}
