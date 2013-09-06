package codeOrchestra.colt.core.ui.components.inputForms

import codeOrchestra.groovyfx.FXBindable
import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.CheckBox

/**
 * @author Dima Kruk
 */
class CTBForm extends InputForm {

    protected CheckBox checkBox = new CheckBox()

    @FXBindable boolean selected

    /*
    <fx:root type="javafx.scene.layout.AnchorPane" xmlns:fx="http://javafx.com/fxml">
      <CheckBox fx:id="checkBox" AnchorPane.leftAnchor="10" AnchorPane.rightAnchor="10" />
      <TextField fx:id="textField" layoutY="23" prefHeight="30" AnchorPane.leftAnchor="10" AnchorPane.rightAnchor="86" />
      <Button fx:id="button" focusTraversable="false" layoutY="23" prefHeight="30" prefWidth="67" styleClass="button" text="Browse" AnchorPane.rightAnchor="10" />
    </fx:root>
     */

    CTBForm() {
        children.addAll(checkBox, textField, button)

        setLeftAnchor(checkBox, 10)
        setRightAnchor(checkBox, 10)

        checkBox.textProperty().bindBidirectional(title())
        buttonDisable().bind(checkBox.selectedProperty().not())
        textDisable().bind(checkBox.selectedProperty().not())

        type = FormType.SIMPLE

        selected().bindBidirectional(checkBox.selectedProperty())
    }

    @Override
    void activateValidation() {
        super.activateValidation()
        checkBox.selectedProperty().addListener({ ObservableValue<? extends Boolean> observableValue, Boolean t, Boolean t1 ->
            if (t1) {
                validateValue()
            } else {
                error = false
            }
        } as ChangeListener)
    }
}
