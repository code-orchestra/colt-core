package codeOrchestra.colt.core.ui.components.inputForms

import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.CheckBox

/**
 * @author Dima Kruk
 */
class CTBForm extends InputForm {

    protected CheckBox checkBox = new CheckBox()

    /*
    <fx:root type="javafx.scene.layout.AnchorPane" xmlns:fx="http://javafx.com/fxml">
      <CheckBox fx:id="checkBox" AnchorPane.leftAnchor="10" AnchorPane.rightAnchor="10" />
      <TextField fx:id="textField" layoutY="23" prefHeight="30" AnchorPane.leftAnchor="10" AnchorPane.rightAnchor="86" />
      <Button fx:id="button" focusTraversable="false" layoutY="23" prefHeight="30" prefWidth="67" styleClass="button" text="Browse" AnchorPane.rightAnchor="10" />
    </fx:root>
     */

    CTBForm() {
        init()

        setLeftAnchor(checkBox, 10)
        setRightAnchor(checkBox, 10)

        buttonDisable().bind(checkBox.selectedProperty().not())
        textDisable().bind(checkBox.selectedProperty().not())

        type = FormType.SIMPLE
    }

     CheckBox getCheckBox() {
        return checkBox
    }
}
