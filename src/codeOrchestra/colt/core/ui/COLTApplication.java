package codeOrchestra.colt.core.ui;

import codeOrchestra.colt.core.COLTException;
import codeOrchestra.colt.core.COLTProjectManager;
import codeOrchestra.colt.core.RecentProjects;
import codeOrchestra.colt.core.errorhandling.ErrorHandler;
import codeOrchestra.colt.core.http.CodeOrchestraRPCHttpServer;
import codeOrchestra.colt.core.http.CodeOrchestraResourcesHttpServer;
import codeOrchestra.colt.core.license.*;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.model.COLTProject;
import codeOrchestra.colt.core.model.listener.ProjectListener;
import codeOrchestra.colt.core.model.monitor.ChangingMonitor;
import codeOrchestra.colt.core.rpc.COLTRemoteServiceServlet;
import codeOrchestra.colt.core.tracker.GAController;
import codeOrchestra.colt.core.ui.dialog.COLTDialogs;
import codeOrchestra.lcs.license.COLTRunningKey;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class COLTApplication extends Application {

    private static COLTApplication instance;

    private static final int SPLASH_WIDTH = 480;
    private static final int SPLASH_HEIGHT = 320;
    private Timeline timeline;

    private Menu recentProjectsSubMenu;

    public static COLTApplication get() {
        return instance;
    }

    private StackPane splashLayout;

    private Stage mainStage;
    private VBox root;
    private Node currentPluginNode;
    private Stage primaryStage;

    public static long timeStarted;

    public ArrayList<MenuItem> menuItems = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;

        this.primaryStage = primaryStage;

        GAController.getInstance().start(primaryStage);
        initSplash();
        initMainStage();

        showSplash();

        timeline = new Timeline(new KeyFrame(new Duration(1000), actionEvent -> {
            timeline.stop();
            Platform.runLater(this::doAfterUIInit);
        }));
        timeline.play();
    }

    private void showSplash() {
        Scene splashScene = new Scene(splashLayout);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        primaryStage.setScene(splashScene);
        primaryStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        primaryStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
        primaryStage.show();
    }

    private void initSplash() {
        splashLayout = new StackPane();
        String imagePath = getClass().getResource("splash.png").toString();
        Image image = new Image(imagePath);
        splashLayout.getChildren().add(new ImageView(image));
        splashLayout.setEffect(new DropShadow());
    }

    private void initMainStage() {
        mainStage = new Stage(StageStyle.DECORATED);
        mainStage.setOnCloseRequest(windowEvent -> {
            if (ChangingMonitor.getInstance().isChanged()) {
                Action action = COLTDialogs.showCloseProjectDialog(primaryStage);

                if (action == Dialog.Actions.CANCEL) {
                    windowEvent.consume();
                } else if (action == Dialog.Actions.YES) {
                    try {
                        COLTProjectManager.getInstance().save();
                    } catch (COLTException e) {
                        ErrorHandler.handle(e, "Can't save project");
                    }
                }
            }

            if (!windowEvent.isConsumed()) {
                dispose();
            }
        });

        root = new VBox();
        root.setFillWidth(true);
        root.setMaxHeight(Double.MAX_VALUE);
        mainStage.setTitle("COLT 1.2");
        mainStage.setScene(new Scene(root, 580, 820));

        Menu fileMenu = new Menu("File");
        MenuItem openProjectMenuItem = new MenuItem("Open Project");
        openProjectMenuItem.setOnAction(t -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("COLT", "*.colt"));
            File file = fileChooser.showOpenDialog(primaryStage.getScene().getWindow());
            if (file != null) {
                try {
                    COLTProjectManager.getInstance().load(file.getPath());
                    ChangingMonitor.getInstance().reset();
                } catch (COLTException e) {
                    ErrorHandler.handle(e, "Can't load the project");
                }
            }
        });

        MenuItem saveProjectMenuItem = new MenuItem("Save Project");
        saveProjectMenuItem.setOnAction(t -> {
            try {
                COLTProjectManager.getInstance().save();
            } catch (COLTException e) {
                ErrorHandler.handle(e, "Can't save the project");
            }
        });
        saveProjectMenuItem.setDisable(true);
        COLTProjectManager.getInstance().addProjectListener(new ProjectListener() {
            @Override
            public void onProjectLoaded(COLTProject project) {
                saveProjectMenuItem.setDisable(false);
            }
            @Override
            public void onProjectUnloaded(COLTProject project) {
                saveProjectMenuItem.setDisable(true);
            }
        });

        MenuItem newProjectMenuItem = new MenuItem("New Project");
        newProjectMenuItem.setOnAction(t -> {
            String projectName = COLTDialogs.showCreateProjectDialog(primaryStage);

            if (projectName != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName(projectName);
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("COLT", "*.colt"));
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file != null) {
                    try {
                        // TODO: a handler must be defined by the user (AS, JS, etc)
                        COLTProjectManager.getInstance().create("AS", projectName, file);
                        ChangingMonitor.getInstance().reset();
                    } catch (COLTException e) {
                        ErrorHandler.handle(e, "Can't create a new project");
                    }
                }
            }
        });

        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(t -> {
            System.exit(0);
        });

        recentProjectsSubMenu = new Menu("Open Recent");
        refreshRecentProjectsMenu();
        COLTProjectManager.getInstance().addProjectListener(new ProjectListener() {
            @Override
            public void onProjectLoaded(COLTProject project) {
                refreshRecentProjectsMenu();
            }
            @Override
            public void onProjectUnloaded(COLTProject project) {
            }
        });

        menuItems.add(newProjectMenuItem);
        menuItems.add(openProjectMenuItem);
        menuItems.add(saveProjectMenuItem);

        fileMenu.getItems().addAll(newProjectMenuItem, new SeparatorMenuItem(), openProjectMenuItem, recentProjectsSubMenu, saveProjectMenuItem, new SeparatorMenuItem(), exitMenuItem);

        Menu helpMenu = new Menu("Help");
        final MenuItem enterSerialItem = new MenuItem("Enter Serial Number");
        enterSerialItem.setOnAction(t -> {
            ExpirationHelper.getExpirationStrategy().showSerialNumberDialog();
        });
        enterSerialItem.setDisable(ExpirationHelper.getExpirationStrategy().isTrialOnly() || !CodeOrchestraLicenseManager.noSerialNumberPresent());
        CodeOrchestraLicenseManager.addListener(() -> {
            enterSerialItem.setDisable(false);
        });
        helpMenu.getItems().add(enterSerialItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);
        menuBar.getMenus().add(helpMenu);
        menuBar.setUseSystemMenuBar(true);

        root.getChildren().add(menuBar);
    }

    private void refreshRecentProjectsMenu() {
        recentProjectsSubMenu.getItems().removeAll();

        List<String> recentProjectsPaths = RecentProjects.getRecentProjectsPaths();
        if (recentProjectsPaths.isEmpty()) {
            recentProjectsSubMenu.setDisable(true);
            return;
        }

        recentProjectsSubMenu.setDisable(false);

        for (String recentProjectsPath : recentProjectsPaths) {
            MenuItem openRecentProjectItem = new MenuItem(recentProjectsPath);

            final File projectFile = new File(recentProjectsPath);
            if (!projectFile.exists() || projectFile.isDirectory()) {
                continue;
            }

            openRecentProjectItem.setOnAction(actionEvent -> {
                try {
                    COLTProjectManager.getInstance().load(projectFile.getPath());
                    ChangingMonitor.getInstance().reset();
                } catch (COLTException e) {
                    ErrorHandler.handle(e, "Can't load a project " + recentProjectsPath);
                }
            });

            recentProjectsSubMenu.getItems().add(openRecentProjectItem);
        }
    }

    private void dispose() {
        COLTRunningKey.setRunning(false);

        LiveCodingHandlerManager.getInstance().dispose();
        CodeOrchestraResourcesHttpServer.getInstance().dispose();
        CodeOrchestraRPCHttpServer.getInstance().dispose();

        Platform.exit();
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

        primaryStage.hide();

        primaryStage = mainStage;
        GAController.getInstance().start(primaryStage);
        primaryStage.show();
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

}
