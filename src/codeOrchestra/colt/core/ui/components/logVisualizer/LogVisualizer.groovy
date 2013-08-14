package codeOrchestra.colt.core.ui.components.logVisualizer

import codeOrchestra.colt.core.ui.components.log.LogFilter
import codeOrchestra.colt.core.ui.components.log.LogMessage
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList as OL
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.web.WebEngine
import javafx.scene.web.WebEvent
import javafx.scene.web.WebView
import netscape.javascript.JSObject

import static codeOrchestra.colt.core.logging.Level.*

/**
 * @author Eugene Potapenko
 */
class LogVisualizer extends VBox {

    private WebView webView = new WebView(contextMenuEnabled: false, prefHeight: 130)
    OL<LogMessage> logMessages
    private boolean htmlLoaded;
    private boolean layoutInited;

    LogVisualizer() {

        String htmlPage = this.class.getResource("html/log-visualizer-webview.html").toExternalForm()
        WebEngine engine = webView.engine
        engine.documentProperty().addListener({ o, oldValue, newValue ->
            htmlLoaded = true
            if (layoutInited && htmlLoaded) {
                // init logic
            }

        } as ChangeListener)
        engine.load(htmlPage)
        children.add(webView)
        setVgrow(webView, Priority.ALWAYS)

        engine.onAlert = new EventHandler<WebEvent<String>>() {
            @Override
            void handle(WebEvent<String> event) {
                println("alert >> " + event.data)
            }
        }
    }

    void setLogMessages(OL<LogMessage> logMessages) {
        this.logMessages = logMessages
        logMessages.addListener({ ListChangeListener.Change<? extends LogMessage> c ->
            synchronized (logMessages) {
                if (htmlLoaded) {
                    while (c.next()) {
                        if (c.wasRemoved()) {
                            println("clear log")
                        } else if (c.wasPermutated()) {
                            println "permutated"
                        } else if (c.wasUpdated()) {
                            println "updated"
                        } else {
                            println("added")
                            addLogMessages()
                        }
                    }
                }
            }

        } as ListChangeListener)
    }

    private JSObject getJSTopObject() {
        (JSObject) webView.engine.executeScript("window")
    }

    private void addLogMessages(List<LogMessage> messages) {
        getJSTopObject().call("alert", [messages])
    }

}
