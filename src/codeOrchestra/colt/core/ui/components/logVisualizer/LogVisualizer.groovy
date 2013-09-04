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

    private WebView webView = new WebView(contextMenuEnabled: false, prefHeight: 130)
    OL<LogMessage> logMessages
    private boolean htmlLoaded
    final private List flushList = []

    @Service LiveCodingManager liveCodingManager

    LogVisualizer() {

        String htmlPage = this.class.getResource("html/log-visualizer-webview.html").toExternalForm()
        WebEngine engine = webView.engine

        engine.documentProperty().addListener({ o, oldValue, newValue ->
            // too early here
            //htmlLoaded = true

        } as ChangeListener)

        engine.load(htmlPage)
        children.add(webView)
        setVgrow(webView, Priority.ALWAYS)

        engine.onAlert = new EventHandler<WebEvent<String>>() {
            @Override
            void handle(WebEvent<String> event) {
                if (event.data == "ready") {
                    htmlLoaded = true;
                    flush()
                } else {
                    println("alert >> " + event.data)
                }
            }
        }

        liveCodingManager.addListener([
                onSessionStart: { session ->
                    ThreadUtils.executeInFXThread({ start() })
                },
                onSessionEnd: { session ->
                    ThreadUtils.executeInFXThread({ stop() })
                },
                onSessionPause: {
                    ThreadUtils.executeInFXThread({ pause() })
                },
                onSessionResume: {
                    ThreadUtils.executeInFXThread({ start() })
                }
        ] as LiveCodingAdapter)
    }

    public void clearMessages() {
        getJSTopObject().call("clearMessages")
    }

    public void start() {
        getJSTopObject().call("start")
    }

    public void stop() {
        getJSTopObject().call("stop")
    }

    public void pause() {
        getJSTopObject().call("pause")
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

    private JSObject getJSTopObject() {
        (JSObject) webView.engine.executeScript("window")
    }

    private void addLogMessages(List<LogMessage> messages) {
        synchronized (flushList) {
            flushList.addAll(messages*.level)
            Platform.runLater {
                flush()
            }
        }
    }

    private flush() {
        synchronized (flushList) {
            if (htmlLoaded && (flushList.size() > 0)) {
                getJSTopObject().call("addLogMessages", flushList)
                flushList.clear()
            }
        }
    }
}
