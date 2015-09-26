package codeOrchestra.colt.core.ui
import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.Version
import codeOrchestra.colt.core.model.monitor.ChangingMonitor
import codeOrchestra.colt.core.ui.dialog.ColtDialogs
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.WindowEvent

/**
 * @author Dima Kruk
 */
class ProjectStage extends Stage {

    Pane root

    boolean disposed

    ProjectStage() {
        root = new VBox()

        setTitle("COLT — Code Orchestra Livecoding Tool (${Version.VERSION})")
        scene = new Scene(root, 480, 768)
        addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, {WindowEvent windowEvent ->
            if (ChangingMonitor.getInstance().isChanged()) {
                ColtDialogs.showCloseProjectDialog(this, windowEvent)
            }
            if (!windowEvent.isConsumed()) {
                dispose()
            }
        } as EventHandler)
        setMaxHeight(Math.min(849, Screen.getPrimary().getVisualBounds().height))
        setMinWidth(460)
    }

    private void dispose() {
        ColtProjectManager.instance.unload()
        disposed = true
    }
}