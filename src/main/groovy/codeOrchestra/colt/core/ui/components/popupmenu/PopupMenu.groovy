package codeOrchestra.colt.core.ui.components.popupmenu

import codeOrchestra.util.SystemInfo
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.geometry.Side
import javafx.scene.control.ContextMenu
import javafx.stage.Popup

/**
 * @author Dima Kruk
 */
class PopupMenu extends Popup {
    PopupMenuContent menuContent
    ContextMenu contextMenu = new ContextMenu()

    PopupMenu() {
        menuContent = new PopupMenuContent()
        content.add(menuContent)

        setAutoHide(true)
        addEventFilter(ActionEvent.ACTION, {
            hide()
        } as EventHandler)
    }

    void show(javafx.scene.Node node) {
        contextMenu.show(node, Side.TOP, 0, 0)
        if (SystemInfo.isMac) {
            contextMenu.hide()
            contextMenu.show(node, Side.TOP, 0, 0)
        } else {
            Platform.runLater({
                contextMenu.hide()
                contextMenu.show(node, Side.TOP, 0, 0)
            })
        }
        //todo: check with new versions of JavaFX
        /*
        if (SystemInfo.isMac) {
            Point2D point = node.parent.localToScreen(node.layoutX, node.layoutY)
            super.show(node, point.x - 17, point.y - menuContent.height - 5)
        }
         */
    }
}
