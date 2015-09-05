package codeOrchestra.colt.core.ui.components.inputForms

import codeOrchestra.colt.core.ui.components.inputForms.base.PasswordInputBase
import javafx.scene.control.Label

/**
 * @author Dima Kruk
 */
class LabeledPasswordInput extends PasswordInputBase {
    protected final Label label = new Label()

    LabeledPasswordInput() {

        setLeftAnchor(label, 19)
        setRightAnchor(label, 48)

        label.textProperty().bindBidirectional(title())

        children.add(label)
    }
}
