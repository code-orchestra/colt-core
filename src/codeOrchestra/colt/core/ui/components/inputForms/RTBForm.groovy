package codeOrchestra.colt.core.ui.components.inputForms

import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.RadioButton

/**
 * @author Dima Kruk
 */
class RTBForm extends InputForm {

    protected RadioButton radioButton = new RadioButton()

    /*
    <fx:root type="javafx.scene.layout.AnchorPane" xmlns:fx="http://javafx.com/fxml">
      <RadioButton fx:id="radioButton" AnchorPane.leftAnchor="10" AnchorPane.rightAnchor="10" />
      <TextField fx:id="textField" layoutY="23" prefHeight="30" AnchorPane.leftAnchor="10" AnchorPane.rightAnchor="86" />
      <Button fx:id="button" focusTraversable="false" layoutY="23" prefHeight="30" prefWidth="67" styleClass="button" text="Browse" AnchorPane.rightAnchor="10" />
    </fx:root>
     */

    RTBForm() {
        children.addAll(radioButton, textField, button)
        init()

        setLeftAnchor(radioButton, 10)
        setRightAnchor(radioButton, 10)

        buttonDisable().bind(radioButton.selectedProperty().not())
        textDisable().bind(radioButton.selectedProperty().not())

        type = FormType.SIMPLE
    }

    RadioButton getRadioButton() {
        return radioButton
    }
}
