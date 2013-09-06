package codeOrchestra.colt.core.ui.components.inputFormsNew


import codeOrchestra.colt.core.ui.components.inputFormsNew.base.InputForm
import javafx.scene.control.Label

/**
 * @author Dima Kruk
 */
class LInputForm extends InputForm {
    protected final Label label = new Label()

    LInputForm() {
        children.addAll(label)

        setLeftAnchor(label, 19)
        setRightAnchor(label, 48)

        label.textProperty().bindBidirectional(title())

        children.add(label)
    }
}
