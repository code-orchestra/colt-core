package codeOrchestra.colt.core.update

import codeOrchestra.colt.core.update.ui.UpdateView
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * @author Dima Kruk
 */
class TestUpdate extends Application{

    @Override
    void start(Stage primaryStage) throws Exception {
        UpdateView root = new UpdateView()

        primaryStage.setTitle("COLT 1.2.6");
        Scene scene = new Scene(root, 580, 820)
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(TestUpdate, args);
    }
}
