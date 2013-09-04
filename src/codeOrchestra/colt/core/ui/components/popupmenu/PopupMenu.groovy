package codeOrchestra.colt.core.ui.components.popupmenu

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.stage.Popup

/**
 * @author Dima Kruk
 */
class PopupMenu extends Popup {
    PopupMenuContent menuContent

    PopupMenu() {
        menuContent = new PopupMenuContent()
        content.add(menuContent)

        setAutoHide(true)
        addEventFilter(ActionEvent.ACTION, {
            hide()
        } as EventHandler)
    }

    void show(javafx.scene.Node node) {
        Point2D point = node.parent.localToScreen(node.layoutX, node.layoutY)
        super.show(node, point.x - 17, point.y - menuContent.height - 10)
    }
}
