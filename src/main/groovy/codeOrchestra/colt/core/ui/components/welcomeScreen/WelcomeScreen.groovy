package codeOrchestra.colt.core.ui.components.welcomeScreen

import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.RecentProjects
import codeOrchestra.colt.core.ui.dialog.ProjectDialogs
import codeOrchestra.util.FileUtils
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.concurrent.Worker
import javafx.event.EventHandler
import javafx.scene.layout.Pane
import javafx.scene.web.PopupFeatures
import javafx.scene.web.WebEngine
import javafx.scene.web.WebEvent
import javafx.scene.web.WebView
import javafx.util.Callback
import netscape.javascript.JSObject

import java.awt.*
import java.util.List

/**
 * @author Eugene Potapenko
 */
class WelcomeScreen extends Pane {

    private WebView webView = new WebView(contextMenuEnabled: false, maxWidth: Double.MAX_VALUE, maxHeight: Double.MAX_VALUE)
    private boolean layoutInited;
    private JSObject windowObject
    private List<File> recentProjects = []

    WelcomeScreen() {
        WebEngine engine = webView.engine
        engine.getLoadWorker().stateProperty().addListener({ o, oldValue, newState ->
            if (newState == Worker.State.SUCCEEDED) {
                windowObject = (JSObject) webView.engine.executeScript("window")
                if (layoutInited) {
                    Platform.runLater{
                        init()
                    }
                }
            }
        } as ChangeListener)

        // {
        // XXX: It's tempting to use load(location.toExternalForm())
        // directly, but it would load a jar: URL when the application
        // is run from a package. As a result, the WebView would
        // prevent us from loading file:// URLs.
        final URL location = getClass().getResource("html/welcome-screen.html")
        String content = FileUtils.getResourceContent(location)
        content = content.replace("\"localresource:../../", "\"${location.toExternalForm().replace("welcomeScreen/html/welcome-screen.html", "")}");
        content = content.replace("\"localresource:./", "\"${location.toExternalForm().replace("welcome-screen.html", "")}");

        engine.loadContent(content)
        // }

        children.add(webView)

        engine.onAlert = { WebEvent<String> event ->
            String[] tokens = event.data.split(":", 2)
            if (tokens.size() == 2) {
                if (tokens[0] == "open") {
                    openBrowser(tokens[1])
                } else if (tokens[0] == "open-project") {
                    ProjectDialogs.openProjectDialog(scene)
                } else if (tokens[0] == "open-recent") {
                    int index = tokens[1] as int
                    ColtProjectManager.instance.load(recentProjects[index].path);
                } else if (tokens[0] == "new-as") {
                    ProjectDialogs.newAsProjectDialog(scene, true)
                } else if (tokens[0] == "open") {
                    ProjectDialogs.openProjectDialog(scene)
                } else if (tokens[0] == "open-demo") {
                    ProjectDialogs.openDemoProjectDialog(scene)
                }
                return
            }
            println("alert >> " + event.data)
        } as EventHandler

        engine.createPopupHandler = { PopupFeatures param ->
            println "try to create popup: $param"
            return null
        } as Callback
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren()
        if (!layoutInited) {
            layoutInited = true
            if (windowObject) {
                init()
            }
        }
    }

    private void init() {
        int count = 0
        for (String recentProjectPath : RecentProjects.getRecentProjectsPaths()) {
            if (count++ < 5) {
                File projectFile = new File(recentProjectPath);
                if (projectFile.exists()) {
                    addRecentProject(projectFile)
                }
            }
        }
    }

    private void addRecentProject(File file) {
        recentProjects << file
        windowObject?.call("addRecentProject", file.name[0..-6], recentProjects.size() - 1)
    }

    private static void openBrowser(String url) {
        Desktop.getDesktop().browse(new URI(url));
    }
}