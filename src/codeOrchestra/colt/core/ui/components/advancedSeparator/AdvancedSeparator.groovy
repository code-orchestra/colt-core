package codeOrchestra.colt.core.ui.components.advancedSeparator

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.Node as FXNode
import javafx.scene.layout.Region
import javafx.scene.layout.VBox

/**
 * @author Dima Kruk
 */
class AdvancedSeparator extends AnchorPane {

    protected Button button
    protected Button saveButton

    boolean close

    FXNode content

    AdvancedSeparator() {
        setMaxWidth(640)
        button = new Button(focusTraversable: false, layoutY: 7, text: "Advanced")
        setLeftAnchor(button, 19)
        saveButton = new Button(focusTraversable: false, layoutY: 7, prefWidth: 97, text: "Save & Run")
        setRightAnchor(saveButton, 10)
        children.addAll(button, saveButton)

        setPadding(new Insets(0, 0, 3, 0))
        VBox.setMargin(this, new Insets(13, 0, 0, 0))

        button.onAction = {
            close = !close
        } as EventHandler

        setClose(true)
    }

    void setClose(boolean close) {
        this.close = close
        if (close) {
            styleClass.remove("fieldset-advanced-caption")
            button.styleClass.remove("btn-hide")
            button.styleClass.add("btn-show")
        } else {
            styleClass.add("fieldset-advanced-caption")
            button.styleClass.remove("btn-show")
            button.styleClass.add("btn-hide")
        }
        content?.visible = content?.managed = !close
    }

    void setContent(Region content) {
        this.content = content
        content.maxWidth = maxWidth
        content.visible = content.managed = !close
        if (!content.styleClass.contains("fieldset-advanced")) {
            content.styleClass.add("fieldset-advanced")
        }
    }

    void setOnAction(EventHandler action) {
        saveButton.onAction = action
    }
}
