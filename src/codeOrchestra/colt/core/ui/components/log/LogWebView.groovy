package codeOrchestra.colt.core.ui.components.log

import javafx.collections.ObservableList as OL
import javafx.scene.control.ScrollPane
import javafx.scene.web.WebView

import static javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER

/**
 * @author Eugene Potapenko
 */
class LogWebView extends ScrollPane {

    WebView webView = new WebView()

    private OL<LogMessage> logMessages

    void setLogList(OL<LogMessage> logMessages) {
        this.logMessages = logMessages
        String htmlPage = this.class.getResource("html/log-webview.html").toExternalForm()
        webView.engine.load(htmlPage)
        webView.prefWidthProperty().bind(this.widthProperty())
        setHbarPolicy(NEVER)
        setContent(webView)
    }
}
