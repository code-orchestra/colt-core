package codeOrchestra.colt.core.ui.components.log

import codeOrchestra.colt.core.logging.Level
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList as OL
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
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
            while (c.next()) {
                if (c.wasRemoved()) {
                    clear()
                } else if (c.wasPermutated()) {
                    println "permutated"
                } else if (c.wasUpdated()) {
                    println "updated"
                } else {
                    c.getAddedSubList().each {
                        addLogMessage(it)
                    }
                }
            }
            filter()
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
                        logMessages.add(new LogMessage("com.codeOrchestra.*:8", WARN, """ListView Selection / Focus APIs To track selection and focus, it is necessary to become familiar with the SelectionModel and FocusModel classes. A ListView has at most one instance of each of these classes, available from selectionModel and focusModel properties respectively. Whilst it is possible to use this API to set a new selection model, in most circumstances this is not necessary - the default selection and focus models should work in most circumstances. The default SelectionModel used when instantiating a ListView is an implementation of the MultipleSelectionModel abstract class. However, as noted in the API documentation for the selectionMode property, the default value is SelectionMode.SINGLE. To enable multiple selection in a default ListView instance, it is therefore necessary to do the following: listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);""", 10, ""))
                        logMessages.add(new LogMessage("com.codeOrchestra.*:8", ERROR, """ListView Selection / Focus APIs To track selection and focus, it is necessary to become familiar with the SelectionModel and FocusModel classes. A ListView has at most one instance of each of these classes, available from selectionModel and focusModel properties respectively. Whilst it is possible to use this API to set a new selection model, in most circumstances this is not necessary - the default selection and focus models should work in most circumstances. The default SelectionModel used when instantiating a ListView is an implementation of the MultipleSelectionModel abstract class. However, as noted in the API documentation for the selectionMode property, the default value is SelectionMode.SINGLE. To enable multiple selection in a default ListView instance, it is therefore necessary to do the following: listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);""", 10, ""))
                        logMessages.add(new LogMessage("com.codeOrchestra.*:8", INFO, """ListView Selection / Focus APIs To track selection and focus, it is necessary to become familiar with the SelectionModel and FocusModel classes. A ListView has at most one instance of each of these classes, available from selectionModel and focusModel properties respectively. Whilst it is possible to use this API to set a new selection model, in most circumstances this is not necessary - the default selection and focus models should work in most circumstances. The default SelectionModel used when instantiating a ListView is an implementation of the MultipleSelectionModel abstract class. However, as noted in the API documentation for the selectionMode property, the default value is SelectionMode.SINGLE. To enable multiple selection in a default ListView instance, it is therefore necessary to do the following: listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);""", 10, ""))
                        logMessages.add(new LogMessage("com.codeOrchestra.*:8", TRACE, """ListView Selection / Focus APIs To track selection and focus, it is necessary to become familiar with the SelectionModel and FocusModel classes. A ListView has at most one instance of each of these classes, available from selectionModel and focusModel properties respectively. Whilst it is possible to use this API to set a new selection model, in most circumstances this is not necessary - the default selection and focus models should work in most circumstances. The default SelectionModel used when instantiating a ListView is an implementation of the MultipleSelectionModel abstract class. However, as noted in the API documentation for the selectionMode property, the default value is SelectionMode.SINGLE. To enable multiple selection in a default ListView instance, it is therefore necessary to do the following: listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);""", 10, ""))
                        println "logMessages = ${logMessages.size()}"
                    } else if (event.code == KeyCode.BACK_SPACE) {
                        logMessages.clear()
                    }
                }
            }
        })
    }

    private void addLogMessage(LogMessage message) {
        if (message) {
            JSObject window = (JSObject) webView.engine.executeScript("window")
            window.call("addLogMessage", message)
        }
    }

    private void clear() {
        JSObject window = (JSObject) webView.engine.executeScript("window")
        window.call("clear")
    }

    private void filter() {
        JSObject window = (JSObject) webView.engine.executeScript("window")
        window.call("filter")
    }
}
