package codeOrchestra.colt.core.ui.components.advancedSeparator

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.Node as FXNode

/**
 * @author Dima Kruk
 */
class AdvancedSeparator extends AnchorPane {

    @FXML Button button
    @FXML Button saveButton

    boolean close

    FXNode content

    AdvancedSeparator() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("advanced_separator.fxml"))
        fxmlLoader.root = this
        fxmlLoader.controller = this

        try {
            fxmlLoader.load()
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

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

    void setContent(javafx.scene.Node content) {
        this.content = content
        content.maxWidth = maxWidth
        content.visible = content.managed = !close
        if (!content.styleClass.contains("fieldset-advanced")) {
            content.styleClass.add("fieldset-advanced")
        }
    }
}
