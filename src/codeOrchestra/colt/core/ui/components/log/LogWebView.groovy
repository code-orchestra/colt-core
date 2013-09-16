package codeOrchestra.colt.core.ui.components.log

import codeOrchestra.colt.core.tracker.GAController
import codeOrchestra.colt.core.ui.components.logVisualizer.LogVisualizer
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
import javafx.scene.shape.Rectangle
import javafx.scene.web.WebEngine
import javafx.scene.web.WebEvent
import javafx.scene.web.WebView
import netscape.javascript.JSObject

import static codeOrchestra.colt.core.logging.Level.*

/**
 * @author Eugene Potapenko
 */
class LogWebView extends VBox {

    private WebView webView = new WebView(contextMenuEnabled: false)
    final OL<LogMessage> logMessages = FXCollections.observableArrayList()
    private boolean layoutInited;
    private LogFilter logFilter
    private LogVisualizer visualizer = new LogVisualizer()
    private JSObject windowObject

    @Override
    protected void layoutChildren() {
        if (!layoutInited) {
            layoutInited = true
            init()
        }
        super.layoutChildren()

        testLog()
    }

    LogWebView() {
        GAController.instance.registerPage(this, "/log.html", "log")
    }

    private void init() {
        WebEngine engine = webView.engine
        engine.load(this.class.getResource("html/log-webview.html").toExternalForm())
//        engine.load("file:///Users/eugenepotapenko/.colt/storage/d6ab2ac7/out/log/html/log-webview.html")
        visualizer.logMessages = logMessages
        visualizer.setMinHeight(Double.NEGATIVE_INFINITY)
        children.add(visualizer)
        children.add(webView)
        setVgrow(webView, Priority.ALWAYS)

        logMessages.addListener({ ListChangeListener.Change<? extends LogMessage> c ->
            while (c.next()) {
                if (c.wasRemoved()) {
//                    println("removed messages: " + c.getRemovedSize())
                    if (c.removedSize == 1) {
//                        println "remove first"
                        removeFirst();
                    } else {
                        clear()
//                        println "clear"
                    }
                } else if (c.wasPermutated()) {
//                    println "permutated"
                } else if (c.wasUpdated()) {
//                    println "updated"
                } else {
                    List<LogMessage> newMessages = []
                    newMessages.addAll(c.getAddedSubList())
                    addLogMessages(newMessages)
                }
            }
            validateLogMessagesSize()
        } as ListChangeListener)

        engine.onAlert = { WebEvent<String> event ->
            String[] tokens = event.data.split(":", 2)
            if (tokens[0] == "command" && tokens.size() == 2) {
                if (tokens[1] == "ready") {
                    Platform.runLater {
                        windowObject = (JSObject) webView.engine.executeScript("window")
                        JSBridge.create(windowObject)
                        if (layoutInited) {
                            addLogMessages(logMessages.asList())
                        }
                    }
                }
            } else {
                println "alert >> " + event.data
            }
        } as EventHandler

        webView.widthProperty().addListener({ v, o, newValue ->
            fireApplicationResize()
        } as ChangeListener)
        webView.heightProperty().addListener({ v, o, newValue ->
            fireApplicationResize()
        } as ChangeListener)
    }

    private void validateLogMessagesSize() {
        Math.max(0, logMessages.size() - 300).times {
            logMessages.remove(0)
        }
    }

    private void addLogMessages(List<LogMessage> messages) {
        Platform.runLater {
            messages*.filter(logFilter ?: LogFilter.ALL)
            windowObject?.call("addLogMessages", messages.findAll { it.message?.trim() =~ /.+/ })
        }
    }

    private void clear() {
        Platform.runLater {
            windowObject?.call("clear")
        }
    }

    private void removeFirst() {
        Platform.runLater {
            windowObject?.call("removeFirst")
        }
    }

    public void filter(LogFilter logFilter) {
        this.logFilter = logFilter
        boolean updated
        logMessages.each {
            if (it.filter(logFilter)) {
                updated = true
            }
        }
        if (updated) {
            Platform.runLater {
                windowObject?.call("filter")
            }
        }
    }

    private void fireApplicationResize() {
        Platform.runLater {
            windowObject?.call("applicationResize")
        }
    }

    private void testLog() {
        addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            void handle(KeyEvent event) {
                if (event.controlDown) {
                    if (event.code == KeyCode.SPACE) {
                        (1).times {
                            Platform.runLater {
                                logMessages.add(new LogMessage("com.codeOrchestra.*:8", INFO, """BUILD SUCCESS""", 10, ""))
//                                logMessages.add(new LogMessage("com.codeOrchestra.*:8", WARN, """ListView Selection / Focus APIs To track selection and focus, it is necessary to become familiar with the SelectionModel and FocusModel classes. A ListView has at most one instance of each of these classes, available from selectionModel and focusModel properties respectively. Whilst it is possible to use this API to set a new selection model, in most circumstances this is not necessary - the default selection and focus models should work in most circumstances. The default SelectionModel used when instantiating a ListView is an implementation of the MultipleSelectionModel abstract class. However, as noted in the API documentation for the selectionMode property, the default value is SelectionMode.SINGLE. To enable multiple selection in a default ListView instance, it is therefore necessary to do the following: listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);""", 10, ""))
//                                logMessages.add(new LogMessage("com.codeOrchestra.*:8", ERROR, """ListView Selection / Focus APIs To track selection and focus, it is necessary to become familiar with the SelectionModel and FocusModel classes. A ListView has at most one instance of each of these classes, available from selectionModel and focusModel properties respectively. Whilst it is possible to use this API to set a new selection model, in most circumstances this is not necessary - the default selection and focus models should work in most circumstances. The default SelectionModel used when instantiating a ListView is an implementation of the MultipleSelectionModel abstract class. However, as noted in the API documentation for the selectionMode property, the default value is SelectionMode.SINGLE. To enable multiple selection in a default ListView instance, it is therefore necessary to do the following: listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);""", 10, ""))
//                                logMessages.add(new LogMessage("com.codeOrchestra.*:8", INFO, """ListView Selection / Focus APIs To track selection and focus, it is necessary to become familiar with the SelectionModel and FocusModel classes. A ListView has at most one instance of each of these classes, available from selectionModel and focusModel properties respectively. Whilst it is possible to use this API to set a new selection model, in most circumstances this is not necessary - the default selection and focus models should work in most circumstances. The default SelectionModel used when instantiating a ListView is an implementation of the MultipleSelectionModel abstract class. However, as noted in the API documentation for the selectionMode property, the default value is SelectionMode.SINGLE. To enable multiple selection in a default ListView instance, it is therefore necessary to do the following: listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);""", 10, ""))
//                                logMessages.add(new LogMessage("com.codeOrchestra.*:8", TRACE, """ListView Selection / Focus APIs To track selection and focus, it is necessary to become familiar with the SelectionModel and FocusModel classes. A ListView has at most one instance of each of these classes, available from selectionModel and focusModel properties respectively. Whilst it is possible to use this API to set a new selection model, in most circumstances this is not necessary - the default selection and focus models should work in most circumstances. The default SelectionModel used when instantiating a ListView is an implementation of the MultipleSelectionModel abstract class. However, as noted in the API documentation for the selectionMode property, the default value is SelectionMode.SINGLE. To enable multiple selection in a default ListView instance, it is therefore necessary to do the following: listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);""", 10, ""))
                            }
                        }
                        println "logMessages = ${logMessages.size()}"
                    } else if (event.code == KeyCode.BACK_SPACE) {
                        logMessages.clear()
                    }
                }
            }
        })
    }
}
