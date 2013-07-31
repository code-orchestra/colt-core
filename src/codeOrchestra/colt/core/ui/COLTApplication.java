package codeOrchestra.colt.core.ui;

import com.aquafx_project.AquaFx;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author Alexander Eliseyev
 */
public class COLTApplication extends Application {

    private Pane root;

    @Override
    public void start(Stage stage) throws Exception {
        root = FXMLLoader.load(getClass().getResource("COLTApplication.fxml"));

        stage.setTitle("COLT 1.1");

        AquaFx.style();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
