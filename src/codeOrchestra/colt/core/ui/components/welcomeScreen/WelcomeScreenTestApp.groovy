package codeOrchestra.colt.core.ui.components.welcomeScreen

import javafx.application.Application
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import org.scenicview.ScenicView

/**
 * @author Eugene Potapenko
 */
class WelcomeScreenTestApp extends Application{
    void start(Stage primaryStage) throws Exception {
        //new groovy.ui.Console().run()
        Parent root = new WelcomeScreen();
        primaryStage.setTitle("COLT — Code Orchestra Livecoding Tool (1.2)");
        Scene scene = new Scene(root, 600, 437)
        primaryStage.setScene(scene);
        ScenicView.show(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(WelcomeScreenTestApp, args);
    }
}


