package codeOrchestra.colt.core.ui.components.inputForms.base

import javafx.scene.control.PasswordField

/**
 * @author Dima Kruk
 */
abstract class PasswordInputBase extends InputWithErrorBase {

    PasswordInputBase() {
        children.remove(textField)
        textField.textProperty().unbindBidirectional(text())


        //region Copy property
        PasswordField field = new PasswordField(layoutY: textField.layoutY, prefHeight: textField.prefHeight)
        setLeftAnchor(field, getLeftAnchor(textField))
        setRightAnchor(field, getRightAnchor(textField))
        //endregion

        textField = field

        textField.textProperty().bindBidirectional(text())

        children.add(textField)
    }
}
