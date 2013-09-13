package codeOrchestra.colt.core.ui.components.logVisualizer

import codeOrchestra.colt.core.LiveCodingManager
import codeOrchestra.colt.core.annotation.Service
import codeOrchestra.colt.core.session.LiveCodingSession
import codeOrchestra.colt.core.session.listener.LiveCodingAdapter
import codeOrchestra.colt.core.ui.components.log.LogMessage
import codeOrchestra.util.ThreadUtils
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList as OL
import javafx.concurrent.Worker
import javafx.event.EventHandler
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.web.WebEngine
import javafx.scene.web.WebEvent
import javafx.scene.web.WebView
import netscape.javascript.JSObject

/**
 * @author Eugene Potapenko
 */
class LogVisualizer extends VBox {

    private WebView webView = new WebView(contextMenuEnabled: false, prefHeight: 120)
    OL<LogMessage> logMessages
    final private List flushList = []

    private JSObject windowObject

    @Service LiveCodingManager liveCodingManager

    LogVisualizer() {

        String htmlPage = this.class.getResource("html/log-visualizer-webview.html").toExternalForm()
        WebEngine engine = webView.engine

        engine.load(htmlPage)
        children.add(webView)
        setVgrow(webView, Priority.ALWAYS)

        engine.onAlert = new EventHandler<WebEvent<String>>() {
            @Override
            void handle(WebEvent<String> event) {
                if (event.data == "ready") {
                    Platform.runLater {
                        windowObject = (JSObject) webView.engine.executeScript("window")
                        flush()
                    }
                } else {
                    println("alert >> " + event.data)
                }
            }
        }

        liveCodingManager.addListener([
                onSessionStart: { session ->
                    Platform.runLater{ start() }
                },
                onSessionEnd: { session ->
                    Platform.runLater{ stop() }
                },
                onSessionPause: {
                    Platform.runLater{ pause() }
                },
                onSessionResume: {
                    Platform.runLater{ start() }
                }
        ] as LiveCodingAdapter)
    }

    public void clearMessages() {
        windowObject?.call("clearMessages")
    }

    public void start() {
        windowObject?.call("start")
    }

    public void stop() {
        windowObject?.call("stop")
    }

    public void pause() {
        windowObject?.call("pause")
    }

    void setLogMessages(OL<LogMessage> logMessages) {
        this.logMessages = logMessages
        logMessages.addListener({ ListChangeListener.Change<? extends LogMessage> c ->
            synchronized (logMessages) {
                // htmlLoaded check moved to flush()
                while (c.next()) {
                    if (c.wasRemoved()) {
                        println("clear log")
                    } else if (c.wasPermutated()) {
                        println "permutated"
                    } else if (c.wasUpdated()) {
                        println "updated"
                    } else {
                        addLogMessages(c.getAddedSubList().asList())
                    }
                }
            }
        } as ListChangeListener)
    }

    private void addLogMessages(List<LogMessage> messages) {
        synchronized (flushList) {
            flushList.addAll(messages*.level)
            flush()
        }
    }

    private flush() {
        synchronized (flushList) {
            if (windowObject && flushList.size() > 0) {
                windowObject.call("addLogMessages", flushList)
                flushList.clear()
            }
        }
    }
}
