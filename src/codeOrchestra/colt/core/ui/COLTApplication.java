package codeOrchestra.colt.core.ui;

import codeOrchestra.colt.core.COLTException;
import codeOrchestra.colt.core.COLTProjectManager;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 * @author Alexander Eliseyev
 */
public class COLTApplication extends Application {

    public static COLTApplication instance;

    private Group root;
    private Node currentPluginNode;

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;

        root = new Group();
        primaryStage.setTitle("COLT 1.1");
        primaryStage.setScene(new Scene(root, 800, 700));

        Menu menu = new Menu("File");
        MenuItem menuLoad = new MenuItem("Load");
        menuLoad.setOnAction(t -> {
            // TODO: file chooser
            try {
                COLTProjectManager.getInstance().load("/Users/eliseyev/TMP/OriginalProject/AwesomeProject.colt2");
            } catch (COLTException e) {
                throw new RuntimeException(e); // TODO: handle nicely
            }
        });

        MenuItem menuSave = new MenuItem("Save");
        menuSave.setOnAction(t -> {

        });

        menu.getItems().addAll(menuLoad, menuSave);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu);
        menuBar.setUseSystemMenuBar(true);

        root.getChildren().add(menuBar);

        primaryStage.show();
    }

    public void setPluginPane(Node node) {
        if (currentPluginNode != null) {
            root.getChildren().remove(currentPluginNode);
        }

        currentPluginNode = node;
        root.getChildren().add(node);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
