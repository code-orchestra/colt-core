package codeOrchestra.colt.core.ui

import codeOrchestra.colt.core.Version
import codeOrchestra.colt.core.tracker.GAController
import codeOrchestra.colt.core.ui.components.welcomeScreen.WelcomeScreen
import codeOrchestra.util.ApplicationUtil
import codeOrchestra.util.SystemInfo
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.WindowEvent

/**
 * @author Dima Kruk
 */
class WelcomeScreenStage extends Stage {

    WelcomeScreen root

    WelcomeScreenStage() {
        root = new WelcomeScreen()
        setTitle("COLT — Code Orchestra Livecoding Tool (" + Version.VERSION + ")")
        scene = SystemInfo.isMac ? new Scene(root, 598, 437) : new Scene(root, 588, 437)
        setResizable(false)

        addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, {WindowEvent windowEvent ->
            dispose()
        }as EventHandler)
        addEventFilter(WindowEvent.WINDOW_SHOWN, {WindowEvent windowEvent ->
            GAController.instance.tracker.trackPageView("/welcome.html", "welcome")
        }as EventHandler)
    }

    public synchronized void dispose() {
        ApplicationUtil.exitColt()
    }
}
