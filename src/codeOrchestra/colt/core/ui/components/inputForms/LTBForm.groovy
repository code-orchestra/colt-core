package codeOrchestra.colt.core.ui.components.inputForms

import codeOrchestra.groovyfx.FXBindable
import javafx.fxml.FXML
import javafx.scene.control.Label

/**
 * @author Dima Kruk
 */
class LTBForm extends InputForm {

    protected Label label = new Label()

    @FXBindable String title;

    /*
    <fx:root type="javafx.scene.layout.AnchorPane" xmlns:fx="http://javafx.com/fxml">
      <Label fx:id="label" AnchorPane.leftAnchor="19" AnchorPane.rightAnchor="48" />
      <TextField fx:id="textField" layoutY="22" prefHeight="30" AnchorPane.leftAnchor="10" AnchorPane.rightAnchor="86" />
      <Button fx:id="button" focusTraversable="false" layoutY="22" prefHeight="30" prefWidth="67" styleClass="button" text="Browse" AnchorPane.rightAnchor="10" />
    </fx:root>
     */

    LTBForm() {
        init()

        setLeftAnchor(label, 19)
        setRightAnchor(label, 48)

        title().bindBidirectional(label.textProperty())

        type = FormType.TEXT_FIELD
    }
}
