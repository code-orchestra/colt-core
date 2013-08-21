package codeOrchestra.colt.core.ui.components.fileset

import codeOrchestra.colt.core.ui.components.log.JSBridge
import codeOrchestra.groovyfx.FXBindable
import codeOrchestra.util.ProjectHelper
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
import javafx.scene.control.TextArea
import javafx.scene.layout.AnchorPane
import javafx.scene.web.WebEngine
import javafx.scene.web.WebEvent
import javafx.scene.web.WebView
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import netscape.javascript.JSObject

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
    private WebView webView = new WebView(id: "fileset-webview", layoutY: 47, prefHeight: 30);
    private TextArea focusRectangle = new TextArea(id: "fileset-webview-focus", layoutY: 46)
    private Button addButton = new Button(contentDisplay: ContentDisplay.GRAPHIC_ONLY, focusTraversable: false, layoutY: 46, prefHeight: 30, prefWidth: 30, text: "Add")
    private JSBridge bridge
    private boolean htmlLoaded
    private boolean layoutInited

    private File startDirectory = null

    @FXBindable boolean useMultiply = true
    @FXBindable boolean addFiles = true
    @FXBindable boolean addDirectory = true
    @FXBindable boolean useExcludes = true

    @FXBindable String files

    FilesetInput() {
        setRightAnchor(addButton, 10)
        setLeftAnchor(label, 19)
        setRightAnchor(label, 48)

        setLeftAnchor(webView, 12)
        setRightAnchor(webView, 50)
        setLeftAnchor(focusRectangle, 10)
        setRightAnchor(focusRectangle, 48)

        addButton.styleClass.add("btn-add")
        label.textProperty().bind(titleProperty)
        children.addAll(label, webView, addButton, focusRectangle)

        focusRectangle.toBack()
        focusRectangle.prefHeightProperty().bind(webView.prefHeightProperty().add(2))
        webView.focusedProperty().addListener({ o, old, focused ->
            focusRectangle.styleClass.removeAll("fileset-webview-focus", "fileset-webview")
            focusRectangle.styleClass.add(focused ? "fileset-webview-focus" : "fileset-webview")
        } as ChangeListener)

        addButton.onAction = {
            ContextMenu cm = buildContextMenu()
            if (cm.items.size() > 1) {
                cm.show(addButton, Side.RIGHT, 0, 0)
            } else {
                cm.items.first().onAction.handle(null)
            }
        } as EventHandler

        // web engine

        String htmlPage = this.class.getResource("html/fileset-webview.html").toExternalForm()
        WebEngine engine = webView.engine
        engine.documentProperty().addListener({ o, oldValue, newValue ->
            htmlLoaded = true
            bridge = new JSBridge(webView.engine) {
                @Override
                void resize(int height) {
                    webView.prefHeight = height
                }
            }
            if (layoutInited && htmlLoaded) {
                // init logic
            }

        } as ChangeListener)
        engine.load(htmlPage)

        engine.onAlert = { WebEvent<String> event ->
            String data = event.data
            if (data.startsWith("command:update")) {
                files = getFilesetHtmlValue()
//                getFilesFromString(files).each {
//                    println("file >> " + it)
//                }
            } else {
                println("alert >> " + data)
            }
        } as EventHandler

        webView.childrenUnmodifiable.addListener({ change ->
            webView.lookupAll(".scroll-bar")*.visible = false
        } as ListChangeListener)
    }

    private ContextMenu buildContextMenu() {
        ContextMenu cm = new ContextMenu()
        if (addFiles) {
            cm.items.add(
                    new MenuItem(text: "Add Files", onAction: { e ->
                        if (useMultiply) {
                            new FileChooser(initialDirectory: getBaseDir()).showOpenMultipleDialog(scene.window).each {
                                startDirectory = it.parentFile
                                addFile(it)
                            }
                        } else {

                            def it = new FileChooser(initialDirectory: getBaseDir()).showOpenDialog(scene.window)
                            startDirectory = it.parentFile
                            addFile(it)
                        }
                    } as EventHandler<ActionEvent>))
        }

        if (addDirectory) {
            cm.items.add(
                    new MenuItem(text: "Add Directory", onAction: { e ->
                        def it = new DirectoryChooser(initialDirectory: getBaseDir()).showDialog(scene.window)
                        startDirectory = it.parentFile
                        addFile(it)

                    } as EventHandler<ActionEvent>))
        }

        if (addFiles && useExcludes) {
            cm.items.add(
                    new MenuItem(text: "Exclude Files", onAction: { e ->
                        new FileChooser(initialDirectory: getBaseDir()).showOpenMultipleDialog(scene.window).each {
                            startDirectory = it.parentFile
                            excludeFile(it)
                        }
                    } as EventHandler<ActionEvent>))
        }

        if (addDirectory && useExcludes) {
            cm.items.add(
                    new MenuItem(text: "Exclude Directory", onAction: { e ->
                        def it = new DirectoryChooser(initialDirectory: getBaseDir()).showDialog(scene.window)
                        startDirectory = it.parentFile
                        excludeFile(it)
                    } as EventHandler<ActionEvent>))
        }

        cm.setStyle("-fx-background-color: rgba(255, 255, 255, .9);");

        return cm
    }

    private JSObject getJSTopObject() {
        (JSObject) webView.engine.executeScript("window")
    }

    private void add(String el) {
        getJSTopObject().call("add", el)
    }

    private void addFile(File file) {
        add(createPattern(file))
    }

    private void excludeFile(File file) {
        add("-" + createPattern(file))
    }

    String getFilesetHtmlValue() {
        "" + getJSTopObject().call("getFiles")
    }

    public static List<File> getFilesFromString(String fileset) {
        if (fileset.isEmpty()) return []

        List<File> result = []
        List<String> filesets = []

        fileset.split(", ").each {
            File file = new File(it)
            if (file.exists()) {
                result.add(file.getAbsoluteFile())
            } else {
                file = new File(baseDir, it)
                if (file.exists()) {
                    result.add(file.getAbsoluteFile())
                }
            }
            filesets << it
        }
        result.addAll(getFilesFromFileset(filesets))
        return result
    }

    private static List<File> getFilesFromFileset(List<String> values) {
        List<File> result = []
        if (values) {
            String baseDir = getBaseDir().getAbsolutePath()
            AntBuilder ant = new AntBuilder()

            def scanner = ant.fileScanner {
                fileset(dir: baseDir) {
                    values.each { String f ->
                        if (f) {
                            if (f.startsWith("-")) {
                                exclude(name: f[1..-1])
                            } else {
                                include(name: f)
                            }
                        }
                    }
                }
            }

            scanner.each {
                result << ((File) it).getAbsoluteFile()
            }
        }

        return result
    }

    private static File getBaseDir() {
        try{
            return ProjectHelper?.currentProject?.baseDir
        }catch (Exception e){
            return new File("/Users/eugenepotapenko/Documents")

        }
    }

    private static String createPattern(File file) {
        return getBaseDir().toURI().relativize(file.toURI()).path
    }
}
