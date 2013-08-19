package codeOrchestra.colt.core.ui.components.fileset

import codeOrchestra.colt.core.ui.components.log.JSBridge
import codeOrchestra.groovyfx.FXBindable
import javafx.beans.value.ChangeListener
import javafx.collections.ListChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.layout.AnchorPane
import javafx.scene.web.WebEngine
import javafx.scene.web.WebEvent
import javafx.scene.web.WebView
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser

/*

<AnchorPane maxWidth="640" GridPane.columnIndex="0" GridPane.rowIndex="1">
    <children>
        <Label layoutY="23" text="Library Paths:" AnchorPane.leftAnchor="19" AnchorPane.rightAnchor="48" />
        <TextArea layoutY="46" prefHeight="30" wrapText="true" AnchorPane.leftAnchor="10" AnchorPane.rightAnchor="48" />
        <Button contentDisplay="graphic_only" focusTraversable="false" layoutY="46" prefHeight="30" prefWidth="30" styleClass="btn-add" text="Add" AnchorPane.rightAnchor="10" />
    </children>
</AnchorPane>


 */

/**
 * @author Eugene Potapenko
 */
class FilesetInput extends AnchorPane {

    @FXBindable String title = "Library Paths:"

    private Label label = new Label(layoutY: 23)
    private WebView webView = new WebView(layoutY: 46, prefHeight: 30);
    private Button addButton = new Button(contentDisplay: ContentDisplay.GRAPHIC_ONLY, focusTraversable: false, layoutY: 46, prefHeight: 30, prefWidth: 30, text: "Add")
    private JSBridge bridge
    private boolean htmlLoaded
    private boolean layoutInited
    private ContextMenu contextMenu = new ContextMenu()


    FilesetInput() {
        setRightAnchor(addButton, 10)
        setLeftAnchor(label, 19)
        setRightAnchor(label, 48)
        setLeftAnchor(webView, 10)
        setRightAnchor(webView, 48)

        addButton.styleClass.add("btn-add")
        label.textProperty().bind(titleProperty)
        children.addAll(label, webView, addButton)

        contextMenu.items.addAll(
                new MenuItem(text: "Add Files", onAction: { e ->
                    new FileChooser().showOpenMultipleDialog(scene.window).each {
                        println("file: " + it)
                    }
                } as EventHandler<ActionEvent>),
                new MenuItem(text: "Add Directory", onAction: { e ->
                    def it = new DirectoryChooser().showDialog(scene.window)
                    println("dir: " + it)

                } as EventHandler<ActionEvent>),
                new MenuItem(text: "Exclude Files", onAction: { e ->
                    new FileChooser().showOpenMultipleDialog(scene.window).each {
                        println("file: " + it)
                    }
                } as EventHandler<ActionEvent>),
                new MenuItem(text: "Exclude Directory", onAction: { e ->
                    def it = new DirectoryChooser().showDialog(scene.window)
                    println("dir: " + it)
                } as EventHandler<ActionEvent>)
        )

        contextMenu.setStyle("-fx-background-color: rgba(255, 255, 255, .9);");

        addButton.onAction = {
            contextMenu.show(addButton, Side.RIGHT, 0, 0)


        } as EventHandler

        // web engine

        String htmlPage = this.class.getResource("html/fileset-webview.html").toExternalForm()
        WebEngine engine = webView.engine
        engine.documentProperty().addListener({ o, oldValue, newValue ->
            htmlLoaded = true
            bridge = new JSBridge(webView.engine)
            if (layoutInited && htmlLoaded) {
                // init logic
            }

        } as ChangeListener)
        engine.load(htmlPage)

        engine.onAlert = new EventHandler<WebEvent<String>>() {
            @Override
            void handle(WebEvent<String> event) {
                println("alert >> " + event.data)
            }
        }

        webView.childrenUnmodifiable.addListener({ change ->
            webView.lookupAll(".scroll-bar")*.visible = false
        } as ListChangeListener)
    }
}
