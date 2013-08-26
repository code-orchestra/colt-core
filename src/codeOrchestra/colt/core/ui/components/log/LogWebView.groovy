package codeOrchestra.colt.core.ui.components.log

import codeOrchestra.colt.core.ui.components.logVisualizer.LogVisualizer
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList as OL
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.VPos
import javafx.scene.control.ScrollPane
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
class LogWebView extends VBox {

    private WebView webView = new WebView(contextMenuEnabled: false)
    final OL<LogMessage> logMessages = FXCollections.observableArrayList()
    private boolean htmlLoaded;
    private boolean layoutInited;
    private LogFilter logFilter
    private LogVisualizer visualizer = new LogVisualizer()


    @Override
    protected void layoutChildren() {
        if (!layoutInited) {
            layoutInited = true
            init()
        }
        super.layoutChildren()
    }

    private void init(){
        String htmlPage = this.class.getResource("html/log-webview.html").toExternalForm()
        WebEngine engine = webView.engine
        engine.documentProperty().addListener({ o, oldValue,  newValue ->
            htmlLoaded = true
            JSBridge.create(engine)
            if (layoutInited && htmlLoaded) {
                addLogMessages(logMessages.asList())
            }

        } as ChangeListener)
        engine.load(htmlPage)
        visualizer.logMessages = logMessages
        children.add(visualizer)
        children.add(webView)
        setVgrow(webView, Priority.ALWAYS)

        logMessages.addListener({ ListChangeListener.Change<? extends LogMessage> c ->
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
                        List<LogMessage> newMessages = []
                        newMessages.addAll(c.getAddedSubList())
                        addLogMessages(newMessages)
                    }
                }
            }
        } as ListChangeListener)

        engine.onAlert = new EventHandler<WebEvent<String>>() {
            @Override
            void handle(WebEvent<String> event) {
                println("alert >> " + event.data)
            }
        }

        //        testLog()

        webView.widthProperty().addListener({ ObservableValue<? extends Number> observable, Number oldValue, Number newValue ->
            fireApplicationResize()
        } as ChangeListener)
        webView.heightProperty().addListener({ ObservableValue<? extends Number> observable, Number oldValue, Number newValue ->
            fireApplicationResize()
        } as ChangeListener)
    }

    LogWebView() {

//        Font.loadFont(this.class.getResource("html/Andale Mono.ttf").toExternalForm(), 12); //todo: загружать нужно в html - @font-face




    }

    private JSObject getJSTopObject() {
        (JSObject) webView.engine.executeScript("window")
    }

    private void addLogMessages(List<LogMessage> messages) {
        messages*.filter(logFilter ?: LogFilter.ALL)
        getJSTopObject().call("addLogMessages", messages)
    }

    private void clear() {
        getJSTopObject().call("clear")
    }

    public void filter(LogFilter logFilter) {
        this.logFilter = logFilter
        boolean updated
        logMessages.each {
            if (it.filter(logFilter)) {
                updated = true
            }
        }
        if (updated && htmlLoaded) {
            getJSTopObject().call("filter")
        }
    }

    private void fireApplicationResize() {
        getJSTopObject().call("applicationResize")
    }

    private void testLog() {
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
}
