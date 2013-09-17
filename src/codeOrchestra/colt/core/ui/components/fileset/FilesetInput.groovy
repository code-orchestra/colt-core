package codeOrchestra.colt.core.ui.components.fileset

import codeOrchestra.colt.core.ui.components.inputForms.markers.MAction
import codeOrchestra.colt.core.ui.components.inputForms.markers.MLabeled
import codeOrchestra.colt.core.ui.components.log.JSBridge
import codeOrchestra.groovyfx.FXBindable
import codeOrchestra.util.ProjectHelper
import javafx.application.Platform
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Side
import javafx.scene.control.*
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
class FilesetInput extends AnchorPane implements MAction, MLabeled {

    @FXBindable String title = "Library Paths:"

    private Label label = new Label()
    private WebView webView = new WebView(id: "fileset-webview", layoutY: 24, prefHeight: 28);
    private TextArea focusRectangle = new TextArea(id: "fileset-webview-focus", layoutY: 23, focusTraversable: false, editable: false, wrapText: true)
    private Button addButton = new Button(contentDisplay: ContentDisplay.GRAPHIC_ONLY, focusTraversable: false, layoutY: 23, prefHeight: 30, prefWidth: 30, text: "Add")
    private JSBridge bridge
    private boolean htmlLoaded

    private File startDirectory = null

    @FXBindable boolean useMultiply = true
    @FXBindable boolean useFiles = true
    @FXBindable boolean useDirectory = true
    @FXBindable boolean useExcludes = true

    @FXBindable String files = ""

    private ContextMenu contextMenu

    boolean fromHtmlUpdate = false

    private JSObject windowObject

    FilesetInput() {

//        styleClass.add("fileset-input")

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

        webView.focusedProperty().addListener({ o, old, f ->
            if (f) {
                requestFocusInHtml()
            }
        } as ChangeListener)

        addButton.onAction = {
            Platform.runLater{
                buildContextMenu()
                if (contextMenu.items.size() > 1) {
                    if (!contextMenu.isShowing()) {
                        contextMenu.show(addButton, Side.RIGHT, 0, 0)
                    }
                } else {
                    contextMenu.items.first().onAction.handle(null)
                }
            }
        } as EventHandler

        Platform.runLater {
            lookupAll(".scroll-bar")*.visible = false
        }

        // web engine

        String htmlPage = this.class.getResource("html/fileset-webview.html").toExternalForm()
        WebEngine engine = webView.engine
        engine.load(htmlPage)

        engine.onAlert = { WebEvent<String> event ->
            String[] tokens = event.data.split(":", 3)
            if (tokens[0] == "command" && tokens.size() > 1) {
                if (tokens[1] == "ready") {
                    Platform.runLater {
                        windowObject = (JSObject) webView.engine.executeScript("window")
                        bridge = new JSBridge(windowObject) {
                            @Override
                            void resize(int height) {
                                Platform.runLater{
                                    if(webView.prefHeight != height){
                                        webView.prefHeight = height
                                    }
                                }
                            }
                        }
                        if (files) {
                            setFilesetHtmlValue(files)
                        }
                    }
                } else if (tokens[1] == ("update")) {
                    String newValue = tokens[2]
                    if (newValue != files) {
                        fromHtmlUpdate = true
                        files = newValue
                        fromHtmlUpdate = false
                    }
                }
            } else {
                println "alert >> " + event.data
            }
        } as EventHandler

        //binding

        filesProperty.addListener({ o, old, String newValue ->
            if (!fromHtmlUpdate) {
                setFilesetHtmlValue(newValue)
            }
        } as ChangeListener)
    }

    void setInputRightAnchor(double value) {
        setRightAnchor(webView, value)
    }

    double getInputRightAnchor() {
        return getRightAnchor(webView)
    }

    private void buildContextMenu() {
        if (contextMenu != null) {
            return
        }

        contextMenu = new ContextMenu()
        if (useFiles) {
            contextMenu.items.add(
                    new MenuItem(text: "Add Files", onAction: { e ->
                        if (useMultiply) {
                            new FileChooser(initialDirectory: getBaseDir()).showOpenMultipleDialog(scene.window).each {
                                startDirectory = it.parentFile
                                addFile(it)
                            }
                        } else {
                            def it = new FileChooser(initialDirectory: getBaseDir()).showOpenDialog(scene.window)
                            if (it) {
                                startDirectory = it.parentFile
                                addFile(it)
                            }
                        }
                    } as EventHandler<ActionEvent>))
        }

        if (useDirectory) {
            contextMenu.items.add(
                    new MenuItem(text: "Add Directory", onAction: { e ->
                        def it = new DirectoryChooser(initialDirectory: getBaseDir()).showDialog(scene.window)
                        if (it) {
                            startDirectory = it?.parentFile
                            addFile(it)
                        }

                    } as EventHandler<ActionEvent>))
        }

        if (useFiles && useExcludes) {
            contextMenu.items.add(
                    new MenuItem(text: "Exclude Files", onAction: { e ->
                        new FileChooser(initialDirectory: getBaseDir()).showOpenMultipleDialog(scene.window).each {
                            startDirectory = it?.parentFile
                            excludeFile(it)

                        }
                    } as EventHandler<ActionEvent>))
        }

        if (useDirectory && useExcludes) {
            contextMenu.items.add(
                    new MenuItem(text: "Exclude Directory", onAction: { e ->
                        def it = new DirectoryChooser(initialDirectory: getBaseDir()).showDialog(scene.window)
                        if (it) {
                            startDirectory = it.parentFile
                            excludeFile(it)
                        }
                    } as EventHandler<ActionEvent>))
        }

        contextMenu.setStyle("-fx-background-color: rgba(255, 255, 255, .9);");
    }


    private void addFile(String el) {
        files += ", " + el
    }

    private void addFile(File file) {
        addFile(createPattern(file))
    }

    private void excludeFile(File file) {
        addFile("-" + createPattern(file))
    }

    private void requestFocusInHtml() {
        Platform.runLater {
            windowObject?.call("requestFocus")
        }
    }

    void setFilesetHtmlValue(String str) {
        Platform.runLater {
            windowObject?.call("setFiles", str)
        }
    }

    public static List<File> getFilesFromString(String fileset, File baseDir = getBaseDir()) {
        if (fileset.isEmpty()) return []
        def (ArrayList<File> result, ArrayList<String> filesets) = collectFiles(fileset, baseDir)
        result.addAll(getFilesFromFileset(filesets, baseDir))
        return result.grep { File f -> !f.directory }
    }

    public static List<File> getDirectoriesFromString(String fileset, File baseDir = getBaseDir()) {
        if (fileset.empty) return []
        def (ArrayList<File> result, ArrayList<String> filesets) = collectFiles(fileset, baseDir)
        result.addAll(getFilesFromFileset(filesets, baseDir))
        return result.grep { File f -> f.directory }
    }

    private static List collectFiles(String fileset, File baseDir) {
        List<File> result = []
        List<String> filesets = []

        fileset.split(", ").each {
            File file = new File(it)
            if (file.exists() && file.absolute) {
                result.add(file.absoluteFile)
            } else {
                file = new File(baseDir, it)
                if (file.exists()) {
                    result.add(file.absoluteFile)
                }
            }
            filesets << it
        }
        [result, filesets]
    }

    private static List<File> getFilesFromFileset(List<String> values, File baseDir = getBaseDir()) {
        List<File> result = []
        if (values) {
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
                result << ((File) it).absoluteFile
            }
        }

        return result
    }

    private static File getBaseDir() {
        try {
            return ProjectHelper.currentProject?.baseDir
        } catch (Exception ignored) {
            return new File("/Users/eugenepotapenko/Documents")

        }
    }

    private static String createPattern(File file) {
        return getBaseDir().toURI().relativize(file.toURI()).path
    }

    public static String createFilesetString(List<File> files) {
        return files.collect { createPattern(it) }.join(", ")
    }

    void setBindProperty(StringProperty value) {
        files().bindBidirectional(value)
    }
}
