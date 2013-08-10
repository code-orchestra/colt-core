package codeOrchestra.colt.core.ui.components.log

import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList as OL
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.shape.Box
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
    private boolean htmlLoaded;
    private boolean layoutInited;
    final private List flushList = []

    @Override
    protected void layoutChildren() {
        super.layoutChildren()
        if (!layoutInited) {
            layoutInited = true
            if (layoutInited && htmlLoaded) {
                List<LogMessage> old = []
                old.addAll(logMessages)
                logMessages.clear()
                logMessages.addAll(old)
            }
        }
    }

    LogWebView() {

        String htmlPage = this.class.getResource("html/log-webview.html").toExternalForm()
        WebEngine engine = webView.engine
        engine.documentProperty().addListener({ o, oldValue, newValue ->
            htmlLoaded = true
            if (layoutInited && htmlLoaded) {
                clear()
                addLogMessages(logMessages.asList())
            }

        } as ChangeListener)
        engine.load(htmlPage)
        children.add(webView)
        setVgrow(webView, Priority.ALWAYS)

        logMessages.addListener({ ListChangeListener.Change<? extends LogMessage> c ->
            synchronized (logMessages) {
                if (htmlLoaded) {
                    while (c.next()) {
                        if (c.wasRemoved()) {
                            println("clear log")
                            clear()
                        } else if (c.wasPermutated()) {
                            println "permutated"
                        } else if (c.wasUpdated()) {
                            println "updated"
                        } else {
                            addLogMessages(c.getAddedSubList().asList())
                        }
                    }
                    filter()
                }
            }

        } as ListChangeListener)

        engine.onAlert = new EventHandler<WebEvent<String>>() {
            @Override
            void handle(WebEvent<String> event) {
                println("alert >> " + event.data)
            }
        }

        addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            void handle(KeyEvent event) {
                if (event.controlDown) {
                    if (event.code == KeyCode.SPACE) {
                        (20).times {
                            logMessages.add(new LogMessage("com.codeOrchestra.*:8", WARN, """ListView Selection / Focus APIs To track selection and focus, it is necessary to become familiar with the SelectionModel and FocusModel classes. A ListView has at most one instance of each of these classes, available from selectionModel and focusModel properties respectively. Whilst it is possible to use this API to set a new selection model, in most circumstances this is not necessary - the default selection and focus models should work in most circumstances. The default SelectionModel used when instantiating a ListView is an implementation of the MultipleSelectionModel abstract class. However, as noted in the API documentation for the selectionMode property, the default value is SelectionMode.SINGLE. To enable multiple selection in a default ListView instance, it is therefore necessary to do the following: listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);""", 10, ""))
                            logMessages.add(new LogMessage("com.codeOrchestra.*:8", ERROR, """ListView Selection / Focus APIs To track selection and focus, it is necessary to become familiar with the SelectionModel and FocusModel classes. A ListView has at most one instance of each of these classes, available from selectionModel and focusModel properties respectively. Whilst it is possible to use this API to set a new selection model, in most circumstances this is not necessary - the default selection and focus models should work in most circumstances. The default SelectionModel used when instantiating a ListView is an implementation of the MultipleSelectionModel abstract class. However, as noted in the API documentation for the selectionMode property, the default value is SelectionMode.SINGLE. To enable multiple selection in a default ListView instance, it is therefore necessary to do the following: listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);""", 10, ""))
                            logMessages.add(new LogMessage("com.codeOrchestra.*:8", INFO, """ListView Selection / Focus APIs To track selection and focus, it is necessary to become familiar with the SelectionModel and FocusModel classes. A ListView has at most one instance of each of these classes, available from selectionModel and focusModel properties respectively. Whilst it is possible to use this API to set a new selection model, in most circumstances this is not necessary - the default selection and focus models should work in most circumstances. The default SelectionModel used when instantiating a ListView is an implementation of the MultipleSelectionModel abstract class. However, as noted in the API documentation for the selectionMode property, the default value is SelectionMode.SINGLE. To enable multiple selection in a default ListView instance, it is therefore necessary to do the following: listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);""", 10, ""))
                            logMessages.add(new LogMessage("com.codeOrchestra.*:8", TRACE, """ListView Selection / Focus APIs To track selection and focus, it is necessary to become familiar with the SelectionModel and FocusModel classes. A ListView has at most one instance of each of these classes, available from selectionModel and focusModel properties respectively. Whilst it is possible to use this API to set a new selection model, in most circumstances this is not necessary - the default selection and focus models should work in most circumstances. The default SelectionModel used when instantiating a ListView is an implementation of the MultipleSelectionModel abstract class. However, as noted in the API documentation for the selectionMode property, the default value is SelectionMode.SINGLE. To enable multiple selection in a default ListView instance, it is therefore necessary to do the following: listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);""", 10, ""))
                        }
                        println "logMessages = ${logMessages.size()}"
                    } else if (event.code == KeyCode.BACK_SPACE) {
                        logMessages.clear()
                    }
                }
            }
        })
    }

    private JSObject getJSTopObject() {
        (JSObject) webView.engine.executeScript("window")
    }

    private void addLogMessages(List messages) {
        synchronized (flushList) {
            boolean flushBefore = flushList.isEmpty()
            flushList.addAll(messages)
            if (flushBefore) {
                Platform.runLater {
                    flush()
                }
            }
        }
    }

    private flush() {
        synchronized (flushList) {
            getJSTopObject().call("addLogMessages", flushList)
            flushList.clear()
        }
    }

    private void clear() {
        Platform.runLater {
            getJSTopObject().call("clear")
        }
    }

    public void filter(LogFilter logFilter) {
        boolean updated
        logMessages.each {
            if(it.filter(logFilter)){
                updated = true
            }
        }
        if(updated){
            Platform.runLater {
                getJSTopObject().call("filter")
            }
        }
    }
}
