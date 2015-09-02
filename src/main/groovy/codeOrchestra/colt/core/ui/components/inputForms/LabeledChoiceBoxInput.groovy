package codeOrchestra.colt.core.ui.components.inputForms

import codeOrchestra.colt.core.ui.components.inputForms.base.TitledInputBase
import codeOrchestra.colt.core.ui.components.inputForms.utils.TextUtil
import codeOrchestra.groovyfx.FXBindable
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.layout.HBox

/**
 * @author Dima Kruk
 */
class LabeledChoiceBoxInput extends TitledInputBase{
    @FXBindable boolean selected

    protected final HBox titleHBox = new HBox(spacing: 10)

    protected final Label label = new Label()
    protected final ChoiceBox<String> choiceBox = new ChoiceBox()

    @FXBindable String value
    @FXBindable javafx.collections.ObservableList<String> values

    LabeledChoiceBoxInput() {
        HBox.setMargin(label, new Insets(8, 0, 0, 0))
        titleHBox.children.addAll(label, choiceBox)
        setLeftAnchor(titleHBox, 10)
        setRightAnchor(titleHBox, 10)

        children.addAll(titleHBox)

        label.textProperty().bindBidirectional(title())

        choiceBox.valueProperty().bindBidirectional(value())
        values().bindBidirectional(choiceBox.itemsProperty())
        choiceBox.selectionModel.selectedItemProperty().addListener({ ObservableValue observableValue, String t, String newValue ->
            if(newValue) {
                choiceBox.prefWidth = TextUtil.getTextWidth(newValue) + 35
            }
        } as ChangeListener)
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
