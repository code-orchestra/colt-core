package codeOrchestra.colt.core.ui;

import codeOrchestra.colt.core.COLTException;
import codeOrchestra.colt.core.COLTProjectManager;
import codeOrchestra.colt.core.http.CodeOrchestraRPCHttpServer;
import codeOrchestra.colt.core.http.CodeOrchestraResourcesHttpServer;
import codeOrchestra.colt.core.license.COLTRunningKey;
import codeOrchestra.colt.core.license.StartupInterceptType;
import codeOrchestra.colt.core.license.StartupInterceptor;
import codeOrchestra.colt.core.model.COLTProject;
import codeOrchestra.colt.core.rpc.COLTRemoteServiceServlet;
import codeOrchestra.colt.core.tasks.COLTTask;
import codeOrchestra.colt.core.tasks.TasksManager;
import codeOrchestra.colt.core.ui.dialog.CreateProjectDialog;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Alexander Eliseyev
 */
public class COLTApplication extends Application {

    private static COLTApplication instance;

    public static COLTApplication get() {
        return instance;
    }

    private VBox root;
    private Node currentPluginNode;
    private Stage primaryStage;

    public static long timeStarted;

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;

        this.primaryStage = primaryStage;

        root = new VBox();
        root.setFillWidth(true);
        root.setMaxHeight(Double.MAX_VALUE);
        primaryStage.setTitle("COLT 1.1");
        primaryStage.setScene(new Scene(root, 800, 700));

        Menu menu = new Menu("File");
        MenuItem menuLoad = new MenuItem("Load");
        menuLoad.setOnAction(t -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("COLT", "*.colt2"));
            File file = fileChooser.showOpenDialog(primaryStage.getScene().getWindow());
            if (file != null) {
                try {
                    COLTProjectManager.getInstance().load(file.getPath());
                } catch (COLTException e) {
                    throw new RuntimeException(e); // TODO: handle nicely
                }
            }
        });

        MenuItem menuSave = new MenuItem("Save");
        menuSave.setOnAction(t -> {
            COLTProject project = COLTProjectManager.getInstance().getCurrentProject();
            if (project != null) {
                String xml = project.toXmlString();
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("COLT", "*.colt2"));
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file != null) {
                    try {
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(xml);
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        MenuItem menuCreate = new MenuItem("Create");
        menuCreate.setOnAction(t -> {
            String projectName = new CreateProjectDialog().show(primaryStage);

            if (projectName != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName(projectName);
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("COLT", "*.colt2"));
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file != null) {
                    try {
                        COLTProjectManager.getInstance().create("AS", projectName, file);
                    } catch (COLTException e) {
                        throw new RuntimeException(e); // TODO: handle nicely
                    }
                }
            }
        });

        menu.getItems().addAll(menuCreate, menuLoad, menuSave);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu);
        menuBar.setUseSystemMenuBar(true);

        root.getChildren().add(menuBar);

        primaryStage.show();

        doAfterUIInit();
    }

    private void doAfterUIInit() {
        // COLT-287
        System.setProperty ("jsse.enableSNIExtension", "false");

        // Intercept start by license check
        StartupInterceptType startupInterceptType = StartupInterceptor.getInstance().interceptStart();
        if (startupInterceptType != StartupInterceptType.START) {
            System.exit(1);
        }

        COLTRunningKey.setRunning(true);

        CodeOrchestraResourcesHttpServer.getInstance().init();

        CodeOrchestraRPCHttpServer.getInstance().init();
        CodeOrchestraRPCHttpServer.getInstance().addServlet(COLTRemoteServiceServlet.getInstance(), "/coltService");
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPluginPane(Node node) {
        if (currentPluginNode != null) {
            root.getChildren().remove(currentPluginNode);
        }

        currentPluginNode = node;
        VBox.setVgrow(currentPluginNode, Priority.ALWAYS);
        root.getChildren().add(node);
    }

    public static void main(String[] args) {
        timeStarted = System.currentTimeMillis();

        launch(args);
    }

    // TODO: do we need dispose app method here?

}
