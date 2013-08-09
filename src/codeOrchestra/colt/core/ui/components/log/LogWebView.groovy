package codeOrchestra.colt.core.ui.components.log

import codeOrchestra.colt.core.logging.Level
import javafx.beans.value.ChangeListener
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList as OL
import javafx.event.EventHandler
import javafx.scene.layout.VBox
import javafx.scene.web.WebEngine
import javafx.scene.web.WebEvent
import javafx.scene.web.WebView
import netscape.javascript.JSObject

import java.util.regex.Pattern

import static codeOrchestra.colt.core.logging.Level.*

/**
 * @author Eugene Potapenko
 */
class LogWebView extends VBox {

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
                    //println "height = $height"
                }

                @Override
                void scroll(int pos) {

                }
            }

            clear()
            logMessages.each {
                addLogMessage(it)
            }
        } as ChangeListener)
        engine.load(htmlPage)
        children.add(webView)

        logMessages.addListener({ ListChangeListener.Change<? extends LogMessage> c ->
            if(c.wasRemoved()){
                clear()
            }
            c.getAddedSubList().each {
                addLogMessage(it)
            }
        } as ListChangeListener)

        engine.onAlert = new EventHandler<WebEvent<String>>() {
            @Override
            void handle(WebEvent<String> event) {
                println("alert >> "+ event.data)
            }
        }
    }

    void addLogMessage(LogMessage message){
        if (message) {
            String messageText = message.message
            String level = "trace"
            switch (message.level) {
                case FATAL:
                case ERROR:
                    level = "error"; break
                case WARN:
                    level = "warning"; break
                case INFO:
                    level = "info"; break
            }
            String source = message.source
            JSObject window = (JSObject) webView.engine.executeScript("window")
            window.call("addLogMessage", messageText, level, source)
        }
    }

    void clear(){
        JSObject window = (JSObject) webView.engine.executeScript("window")
        window.call("clear")
//        webView.engine.executeScript("clear()")
    }
}
