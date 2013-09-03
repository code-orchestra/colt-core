package codeOrchestra.colt.core.ui

import codeOrchestra.colt.core.ui.components.welcomeScreen.WelcomeScreen
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * @author Dima Kruk
 */
class WelcomeScreenStage extends Stage {

    WelcomeScreen root

    WelcomeScreenStage() {
        root = new WelcomeScreen()
        setTitle("COLT — Code Orchestra Livecoding Tool (1.2)")
        scene = new Scene(root, 600, 437)
        setResizable(false)
    }
}
