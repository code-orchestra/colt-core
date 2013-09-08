package codeOrchestra.colt.core.ui

import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.execution.OSProcessHandler
import codeOrchestra.colt.core.http.CodeOrchestraRPCHttpServer
import codeOrchestra.colt.core.http.CodeOrchestraResourcesHttpServer
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager
import codeOrchestra.colt.core.tasks.TasksManager
import codeOrchestra.colt.core.ui.components.welcomeScreen.WelcomeScreen
import codeOrchestra.lcs.license.ColtRunningKey
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.WindowEvent

/**
 * @author Dima Kruk
 */
class WelcomeScreenStage extends Stage {

    WelcomeScreen root
    private boolean disposed

    WelcomeScreenStage() {
        root = new WelcomeScreen()
        setTitle("COLT — Code Orchestra Livecoding Tool (1.2)")
        scene = new Scene(root, 598, 437)
        setResizable(false)

        addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, {WindowEvent windowEvent ->
            dispose()
        }as EventHandler)
    }

    public synchronized void dispose() {
        if (disposed) {
            return;
        }

        ColtRunningKey.setRunning(false);

        TasksManager.getInstance().dispose();
        ColtProjectManager.getInstance().dispose();
        LiveCodingHandlerManager.getInstance().dispose();
        CodeOrchestraResourcesHttpServer.getInstance().dispose();
        CodeOrchestraRPCHttpServer.getInstance().dispose();
        OSProcessHandler.dispose();

        disposed = true;

        Platform.exit();
    }
}
