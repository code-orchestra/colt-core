package codeOrchestra.colt.core.ui.components

import javafx.scene.control.ContentDisplay
import javafx.scene.control.ToggleButton

/**
 * @author Dima Kruk
 */
class StatusButton extends ToggleButton {
    StatusButton() {
        this.focusTraversable = false
        this.contentDisplay = ContentDisplay.GRAPHIC_ONLY
        setPrefSize(32, 31)
        this.styleClass.add("btn-live")
    }
}
