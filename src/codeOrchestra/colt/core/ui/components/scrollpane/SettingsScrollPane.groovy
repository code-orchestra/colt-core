package codeOrchestra.colt.core.ui.components.scrollpane

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.geometry.Bounds
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox

/**
 * @author Dima Kruk
 */
class SettingsScrollPane extends ScrollPane {

    protected VBox mainContainer

    SettingsScrollPane() {
        setId("settings-form")
        styleClass.add("scroll-pane-settings")

        setFitToWidth(true)

        mainContainer = new VBox()
        mainContainer.alignment = Pos.TOP_CENTER

        setContent(mainContainer)

        //fix for windows
        mainContainer.heightProperty().addListener({ javafx.beans.Observable observable ->
            Platform.runLater{
                requestLayout()
            }
        } as InvalidationListener)
    }

    protected scrollToNode(Parent node) {
        Bounds bounds = content.boundsInLocal
        Bounds nodeBounds = content.sceneToLocal(node.localToScene(node.layoutBounds))

        setVvalue(nodeBounds.minY/bounds.height)
    }
}
