package codeOrchestra.colt.core.ui.components.welcomeScreen

import codeOrchestra.colt.core.ColtException
import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.RecentProjects
import codeOrchestra.colt.core.tracker.GAController
import codeOrchestra.colt.core.ui.components.log.JSBridge
import codeOrchestra.colt.core.ui.dialog.ProjectDialogs
import javafx.beans.value.ChangeListener
import javafx.event.EventHandler
import javafx.scene.layout.Pane
import javafx.scene.web.PopupFeatures
import javafx.scene.web.WebEngine
import javafx.scene.web.WebEvent
import javafx.scene.web.WebView
import javafx.util.Callback
import netscape.javascript.JSObject

import java.awt.Desktop

/**
 * @author Eugene Potapenko
 */
class WelcomeScreen extends Pane {

    private WebView webView = new WebView(contextMenuEnabled: false, maxWidth: Double.MAX_VALUE, maxHeight: Double.MAX_VALUE)
    private boolean layoutInited;
    private boolean htmlLoaded;
    private List<File> recentProjects = []

    WelcomeScreen() {
        GAController.instance.registerPage(this, "/welcome.html", "welcome")

        WebEngine engine = webView.engine
        engine.documentProperty().addListener({ o, oldValue, newValue ->
            htmlLoaded = true
            JSBridge.create(engine)
            if (layoutInited && htmlLoaded) {
                init()
            }

        } as ChangeListener)

        engine.load(this.class.getResource("html/welcome-screen.html").toExternalForm())
        children.add(webView)

        engine.onAlert = { WebEvent<String> event ->
            String[] tokens = event.data.split(":", 2)
            if (tokens.size() == 2) {
                if (tokens[0] == "open") {
                    openBrowser(tokens[1])
                }else if (tokens[0] == "open-project") {
                    int index = tokens[1] as int
                    ColtProjectManager.getInstance().load(recentProjects[index].getPath());
                }else if (tokens[0] == "new-js") {
                    ProjectDialogs.newJsProjectDialog(scene)
                }else if (tokens[0] == "new-as") {
                    ProjectDialogs.newAsProjectDialog(scene)
                }else if (tokens[0] == "open") {
                    ProjectDialogs.openProjectDialog(scene)
                }else if (tokens[0] == "open-demo") {
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
            if (layoutInited && htmlLoaded) {
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

    private JSObject getJSTopObject() {
        (JSObject) webView.engine.executeScript("window")
    }

    private void addRecentProject(File file) {
        recentProjects << file
        getJSTopObject().call("addRecentProject", file.name[0..-6], recentProjects.size() - 1)
    }

    private static void openBrowser(String url) {
        Desktop.getDesktop().browse(new URI(url));
    }
}
