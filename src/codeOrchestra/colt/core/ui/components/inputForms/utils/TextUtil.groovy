package codeOrchestra.colt.core.ui.components.inputForms.utils

import javafx.scene.text.Text


/**
 * @author Dima Kruk
 */
class TextUtil {
    static int getTextWidth(String str) {
        final Text text = new Text(str)
        text.snapshot(null, null)
        return text.getLayoutBounds().getWidth()
    }
}
