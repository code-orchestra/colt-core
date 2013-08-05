package codeOrchestra.colt.core.ui.components.fileset

import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.web.WebView

/**
 * @author Eugene Potapenko
 */
class FilesetInput extends HBox {
    private WebView webView = new WebView();
    private Button botton = new Button(text: "+")

    FilesetInput() {
        children.add(webView)
        children.add(botton)
        setHgrow(webView, Priority.ALWAYS)
    }
}
