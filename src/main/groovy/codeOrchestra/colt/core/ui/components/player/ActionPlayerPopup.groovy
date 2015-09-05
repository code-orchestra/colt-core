package codeOrchestra.colt.core.ui.components.player

import javafx.geometry.Point2D
import javafx.stage.Popup
import javafx.scene.Node as FXNode

/**
 * @author Dima Kruk
 */
class ActionPlayerPopup extends Popup {
    ActionPlayer actionPlayer = new ActionPlayer()

    ActionPlayerPopup() {
        content.add(actionPlayer)
        setAutoHide(true)
    }

    void show(FXNode node) {
        Point2D point = node.parent.localToScreen(node.layoutX, node.layoutY)
        super.show(node, point.x + 49, point.y - 10)
    }
}
