package codeOrchestra.colt.core.ui.components.log

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList as OL
import javafx.concurrent.Worker
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle
import javafx.scene.web.PopupFeatures
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import javafx.util.Callback

/**
 * @author Eugene Potapenko
 */
class LogWebView extends HBox {

    WebView webView = new WebView(contextMenuEnabled: false)
    private OL<LogMessage> logMessages

    OL<LogMessage> getLogList() {
        return logMessages
    }

    void setLogList(OL<LogMessage> logMessages) {
        this.logMessages = logMessages

        String htmlPage = this.class.getResource("html/log-webview.html").toExternalForm()
        WebEngine engine = webView.engine
        engine.documentProperty().addListener({ o, oldValue, newValue ->
            new JSBridge(engine) {
                @Override
                void resize(int height) {
                    println "height = $height"
                }

                @Override
                void scroll(int pos) {

                }
            }
        } as ChangeListener)
        engine.load(htmlPage)
        children.add(webView)
    }
}
