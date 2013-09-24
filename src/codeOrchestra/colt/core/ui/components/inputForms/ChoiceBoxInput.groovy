package codeOrchestra.colt.core.ui.components.inputForms

import codeOrchestra.colt.core.ui.components.inputForms.base.TitledInputBase
import codeOrchestra.colt.core.ui.components.inputForms.markers.MChoiceBox
import codeOrchestra.groovyfx.FXBindable
import javafx.beans.property.StringProperty
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.collections.ObservableList as FXObservableList

/**
 * @author Dima Kruk
 */
class ChoiceBoxInput extends TitledInputBase implements MChoiceBox {

    protected final Label label = new Label()
    protected ChoiceBox choiceBox = new ChoiceBox(layoutY: 22.0, prefHeight: 30)

    @FXBindable String value
    @FXBindable FXObservableList<String> values

    ChoiceBoxInput() {
        setLeftAnchor(label, 19)
        setRightAnchor(label, 48)
        children.add(label)

        setLeftAnchor(choiceBox, 10)
        children.add(choiceBox)

        label.textProperty().bindBidirectional(title())

        choiceBox.valueProperty().bindBidirectional(value())
        values().bindBidirectional(choiceBox.itemsProperty())
    }

    void setValues(List<String> values) {
        this.values.clear()
        values.each {
            this.values.add(it)
        }
    }

    void setBindProperty(StringProperty value) {
       this.value().bindBidirectional(value)
    }

}
