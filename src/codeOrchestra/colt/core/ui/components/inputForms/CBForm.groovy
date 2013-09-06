package codeOrchestra.colt.core.ui.components.inputForms

import codeOrchestra.colt.core.ui.groovy.GroovyDynamicMethods
import codeOrchestra.groovyfx.FXBindable
import javafx.beans.value.ChangeListener
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox

/**
 * @author Dima Kruk
 */
class CBForm extends AnchorPane implements ITypedForm {
    protected CheckBox checkBox = new CheckBox(prefWidth: -1.0, text: "use the latest version")
    protected ChoiceBox choiceBox = new ChoiceBox(layoutY: 22.0)
    protected Label errorLabel = new Label(layoutY:30.0, text:"Incorrect Flex SDK path specified" )

    FormType type

    /*
    <fx:root type="javafx.scene.layout.AnchorPane" xmlns:fx="http://javafx.com/fxml/1" xmlns="http://javafx.com/javafx/2.2">
      <HBox id="HBox" alignment="CENTER_LEFT" layoutY="-1.0" spacing="0.0" AnchorPane.leftAnchor="19.0">
        <children>
          <Label fx:id="label" text="Target player version (" />
          <CheckBox fx:id="checkBox" prefWidth="-1.0" text="use the latest version" />
          <Label text="):" />
        </children>
      </HBox>
      <ChoiceBox fx:id="choiceBox" layoutY="22.0" AnchorPane.leftAnchor="10.0" />
      <Label fx:id="errorLabel" layoutY="30.0" styleClass="error-label" text="Incorrect Flex SDK path specified" AnchorPane.leftAnchor="125.0" AnchorPane.rightAnchor="10.0" />
    </fx:root>
     */

    @FXBindable boolean selected
    @FXBindable String value
    @FXBindable List<String> values
    @FXBindable String errorMessage

    CBForm() {
        GroovyDynamicMethods.init()

        HBox hb;

        children.addAll(
                hb = new HBox(
                        alignment: Pos.CENTER_LEFT, layoutX: -1.0, spacing: 0.0,
                        newChildren: [
                                new Label(text: "Target player version ("), checkBox, new Label(text: "):")
                        ]
                ),
                choiceBox, errorLabel
        )

        errorLabel.styleClass.add("error-label")

        setLeftAnchor(hb, 19)
        setLeftAnchor(choiceBox, 10)
        setRightAnchor(checkBox, 10)
        setLeftAnchor(choiceBox, 10)
        setLeftAnchor(errorLabel, 125)
        setRightAnchor(errorLabel, 10)

        choiceBox.disableProperty().bind(checkBox.selectedProperty())

        checkBox.selectedProperty().bindBidirectional(selected())
        choiceBox.valueProperty().bindBidirectional(value())
        values = choiceBox.items

        errorMessage().addListener({ v, o, newValue ->
            String value = newValue ?: ""
            errorLabel.visible = !value.empty
            errorLabel.text = value
        } as ChangeListener)

        type = FormType.CHOICE_BOX
    }
}
