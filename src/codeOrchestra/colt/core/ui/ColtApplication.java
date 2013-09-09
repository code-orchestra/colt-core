package codeOrchestra.colt.core.ui;

import codeOrchestra.colt.core.ColtException;
import codeOrchestra.colt.core.ColtProjectManager;
import codeOrchestra.colt.core.RecentProjects;
import codeOrchestra.colt.core.execution.OSProcessHandler;
import codeOrchestra.colt.core.http.CodeOrchestraRPCHttpServer;
import codeOrchestra.colt.core.http.CodeOrchestraResourcesHttpServer;
import codeOrchestra.colt.core.license.*;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceServlet;
import codeOrchestra.colt.core.tasks.TasksManager;
import codeOrchestra.colt.core.tracker.GAController;
import codeOrchestra.lcs.license.ColtRunningKey;
import codeOrchestra.util.ApplicationUtil;
import codeOrchestra.util.StringUtils;
import com.sun.javafx.css.StyleManager;
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

/**
 * @author Alexander Eliseyev
 */
public class ColtApplication extends Application {

    private static ColtApplication instance;

    private static final int SPLASH_WIDTH = 480;
    private static final int SPLASH_HEIGHT = 320;
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

    public ProjectStage getMainStage() {
        return mainStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;

        this.primaryStage = primaryStage;

        StyleManager.getInstance().addUserAgentStylesheet("/codeOrchestra/colt/core/ui/style/main.css");

        GAController.getInstance().start(primaryStage);

        if (!startWasRecentlyRequested) {
            initSplash();
        }

        welcomeScreenStage = new WelcomeScreenStage();

        menuBar = new ColtMenuBar();
        menuBar.setUseSystemMenuBar(true);

        mainStage = new ProjectStage();
        mainStage.getRoot().getChildren().add(menuBar);
        mainStage.setOnCloseRequest(windowEvent -> {
            dispose();
        });

        if (!startWasRecentlyRequested) {
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

    private void doAfterUIInit() {
        // COLT-287
        System.setProperty("jsse.enableSNIExtension", "false");

        // Intercept start by license check
        StartupInterceptType startupInterceptType = StartupInterceptor.getInstance().interceptStart();
        if (startupInterceptType != StartupInterceptType.START) {
            System.exit(1);
        }

        ColtRunningKey.setRunning(true);

        CodeOrchestraResourcesHttpServer.getInstance().init();

        CodeOrchestraRPCHttpServer.getInstance().init();
        CodeOrchestraRPCHttpServer.getInstance().addServlet(ColtRemoteServiceServlet.getInstance(), "/coltService");

        primaryStage.hide();

        // Open most recent project
        boolean opened = false;
        if (RecentProjects.mustOpenRecentProject()) {
            for (String recentProjectPath : RecentProjects.getRecentProjectsPaths()) {
                File projectFile = new File(recentProjectPath);
                if (projectFile.exists()) {
                    try {
                        ColtProjectManager.getInstance().load(projectFile.getPath());
                        opened = true;
                        break;
                    } catch (ColtException e) {
                        // ignore
                    }
                }
            }
        }
        if (!opened) {
            showWelcomeScreen();
        }
    }

    public void closeProject() {
        if (mainStage.isShowing()) {
            mainStage.hide();
            RecentProjects.setMustOpenRecentProject(false);
        }
    }

    public void showWelcomeScreen() {
        primaryStage = welcomeScreenStage;
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPluginPane(Node node) {
        if (primaryStage == welcomeScreenStage) {
            primaryStage.hide();
        }
        if (primaryStage != mainStage) {
            primaryStage = mainStage;
            GAController.getInstance().start(primaryStage);
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

        if (StringUtils.isEmpty(System.getProperty("colt.handlers"))) {
            System.setProperty("colt.handlers", "AS:codeOrchestra.colt.as.ASLiveCodingLanguageHandler,JS:codeOrchestra.colt.js.JSLiveCodingLanguageHandler");
        }

        launch(args);
    }

    public ColtMenuBar getMenuBar() {
        return menuBar;
    }


}
