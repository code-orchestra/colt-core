package codeOrchestra.colt.core.ui;

import codeOrchestra.colt.core.ColtException;
import codeOrchestra.colt.core.ColtProjectManager;
import codeOrchestra.colt.core.RecentProjects;
import codeOrchestra.colt.core.execution.OSProcessHandler;
import codeOrchestra.colt.core.http.CodeOrchestraRPCHttpServer;
import codeOrchestra.colt.core.http.CodeOrchestraResourcesHttpServer;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.model.ProjectHandlerIdParser;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceServlet;
import codeOrchestra.colt.core.tasks.TasksManager;
import codeOrchestra.colt.core.ui.dialog.ColtDialogs;
import codeOrchestra.colt.core.ui.dialog.UpdateDialog;
import codeOrchestra.colt.core.update.tasks.UpdateManager;
import codeOrchestra.colt.core.update.tasks.UpdateTask;
import codeOrchestra.lcs.license.ColtRunningKey;
import codeOrchestra.util.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Alexander Eliseyev
 */
public class ColtApplication extends Application {

    private static ColtApplication instance;

    private static final int SPLASH_WIDTH = 480;
    private static final int SPLASH_HEIGHT = 320;
    public static boolean IS_PLUGIN_MODE = false;
    private Timeline timeline;

    private ColtMenuBar menuBar;

    public static ColtApplication get() {
        return instance;
    }

    private boolean disposed;

    private Stage primaryStage;
    private StackPane splashLayout;
    private WelcomeScreenStage welcomeScreenStage;
    private ProjectStage mainStage;
    private Node currentPluginNode;

    private boolean startWasRecentlyRequested = ApplicationUtil.coltStartWasRecentlyRequested();

    public static long timeStarted;

    public String path;

    public ProjectStage getMainStage() {
        return mainStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;

        this.primaryStage = primaryStage;
        Application.setUserAgentStylesheet("/codeOrchestra/colt/core/ui/style/main.css");

        menuBar = new ColtMenuBar();

        if (RecentProjects.mustOpenRecentProject()) {
            for (String recentProjectPath : RecentProjects.getRecentProjectsPaths()) {
                File projectFile = new File(recentProjectPath);
                if (projectFile.exists()) {
                    this.path = projectFile.getPath();
                    IS_PLUGIN_MODE = new ProjectHandlerIdParser(FileUtils.read(projectFile)).getIsPlugin();
                    break;
                }
            }
        }

        if (!(startWasRecentlyRequested || IS_PLUGIN_MODE)) {
            initSplash();
            showSplash();
            timeline = new Timeline(new KeyFrame(new Duration(1000), actionEvent -> {
                timeline.stop();
                Platform.runLater(this::doAfterUIInit);
            }));
            timeline.play();
        } else {
            doAfterUIInit();
        }
    }

    private void initSplash() {
        splashLayout = new StackPane();
        String imagePath = getClass().getResource("splash.png").toString();
        Image image = new Image(imagePath);
        splashLayout.getChildren().add(new ImageView(image));
        splashLayout.setEffect(new DropShadow());
    }

    private void showSplash() {
        Scene splashScene = new Scene(splashLayout);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        primaryStage.setScene(splashScene);
        primaryStage.setX(bounds.getMinX() + (bounds.getWidth() - SPLASH_WIDTH) / 2);
        primaryStage.setY(bounds.getMinY() + (bounds.getHeight() - SPLASH_HEIGHT) / 2);
        primaryStage.show();
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

    public boolean checkForUpdate(boolean showMessage) {
        ArrayList<UpdateTask> updateTasks = UpdateManager.checkForUpdate();
        if (updateTasks != null) {
            if (updateTasks.size() > 0) {
                UpdateDialog dialog = new UpdateDialog(primaryStage);
                dialog.setListOfTasks(updateTasks);
                dialog.setMessage("New update is available");
                dialog.show();
                if (dialog.isSuccess) {
                    try {
                        ApplicationUtil.restartColt();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (showMessage) {
                ColtDialogs.showError(primaryStage, "COLT update", "Can't reach the update server.",
                        "Make sure your internet connection is active.");
            }
            return false;
        }
        if (showMessage) {
            ColtDialogs.showInfo(primaryStage, "COLT update", "You have the latest version of COLT.");
        }
        return false;
    }

    private void doAfterUIInit() {
        // COLT-287
        System.setProperty("jsse.enableSNIExtension", "false");
        System.setProperty("file.encoding", "UTF-8");

        ColtRunningKey.setRunning(true);
        new Thread() {
            @Override
            public void run() {
                CodeOrchestraRPCHttpServer.getInstance().init();
                CodeOrchestraRPCHttpServer.getInstance().addServlet(ColtRemoteServiceServlet.getInstance(), "/coltService");
            }
        }.start();

        primaryStage.hide();

        if (path != null) {
            initProjectStage();
            primaryStage = mainStage;
            primaryStage.show();
            new Thread() {
                @Override
                public void run() {
                    // Open most recent project
                    if (path != null) {
                        Platform.runLater(() -> {
                            try {
                                ColtProjectManager.getInstance().load(path);
                            } catch (ColtException e) {
                                e.printStackTrace();
                                primaryStage.hide();
                                showWelcomeScreen();
                            }
                        });
                    }
                }
            }.start();
        } else {
            showWelcomeScreen();
        }

        new Thread(() -> {
            ThreadUtils.sleep(10000);
            Platform.runLater(() -> checkForUpdate(false));
        }).start();
    }

    public void showWelcomeScreen() {
        if (welcomeScreenStage == null) {
            welcomeScreenStage = new WelcomeScreenStage();
        }
        primaryStage = welcomeScreenStage;
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void initProjectStage() {
        mainStage = new ProjectStage();
        mainStage.getRoot().getChildren().add(menuBar);
        mainStage.setOnCloseRequest(windowEvent -> dispose());
    }

    public void setPluginPane(Node node) {
        if (primaryStage instanceof WelcomeScreenStage) {
            primaryStage.hide();
        }
        if (mainStage == null) {
            initProjectStage();
        }
        if (primaryStage != mainStage) {
            primaryStage = mainStage;
            primaryStage.show();
        }
        if (currentPluginNode != null) {
            mainStage.getRoot().getChildren().remove(currentPluginNode);
        }

        currentPluginNode = node;
        VBox.setVgrow(currentPluginNode, Priority.ALWAYS);
        mainStage.getRoot().getChildren().add(node);
    }

    public static void main(String[] args) {
        timeStarted = System.currentTimeMillis();

        // Handle file argument
        if (args != null) {
            for (String arg : args) {
                if (arg.startsWith("--launcher.openFile")) {
                    String path = arg.split("=")[1];
                    if (path != null) {
                        RecentProjects.addRecentProject(path);
                        IS_PLUGIN_MODE = true;
                        break;
                    }
                }
            }
        }

        if (StringUtils.isEmpty(System.getProperty("colt.handlers"))) {
            System.setProperty("colt.handlers", "AS:codeOrchestra.colt.as.ASLiveCodingLanguageHandler");
        }

        launch(args);
    }

    public ColtMenuBar getMenuBar() {
        return menuBar;
    }

}