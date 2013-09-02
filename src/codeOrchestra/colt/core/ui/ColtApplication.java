package codeOrchestra.colt.core.ui;

import codeOrchestra.colt.core.ColtException;
import codeOrchestra.colt.core.ColtProjectManager;
import codeOrchestra.colt.core.RecentProjects;
import codeOrchestra.colt.core.errorhandling.ErrorHandler;
import codeOrchestra.colt.core.http.CodeOrchestraRPCHttpServer;
import codeOrchestra.colt.core.http.CodeOrchestraResourcesHttpServer;
import codeOrchestra.colt.core.license.*;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;
import codeOrchestra.colt.core.model.monitor.ChangingMonitor;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceServlet;
import codeOrchestra.colt.core.tracker.GAController;
import codeOrchestra.colt.core.ui.dialog.ColtDialogs;
import codeOrchestra.lcs.license.ColtRunningKey;
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
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;

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

    private StackPane splashLayout;

    private Stage mainStage;
    private VBox root;
    private Node currentPluginNode;
    private Stage primaryStage;

    public static long timeStarted;

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;

        this.primaryStage = primaryStage;

        StyleManager.getInstance().addUserAgentStylesheet("/codeOrchestra/colt/core/ui/style/main.css");

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
                ColtDialogs.showCloseProjectDialog(primaryStage, windowEvent);
            }

            if (!windowEvent.isConsumed()) {
                dispose();
            }
        });

        root = new VBox();
        root.setFillWidth(true);
        root.setMaxHeight(Double.MAX_VALUE);
        mainStage.setTitle("COLT - Code Orchestra Livecoding Tool (1.2)");
        mainStage.setScene(new Scene(root, 506, 820));

        menuBar = new ColtMenuBar();
        root.getChildren().add(menuBar);

    }

    private void dispose() {
        ColtRunningKey.setRunning(false);

        ColtProjectManager.getInstance().dispose();
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

        ColtRunningKey.setRunning(true);

        CodeOrchestraResourcesHttpServer.getInstance().init();

        CodeOrchestraRPCHttpServer.getInstance().init();
        CodeOrchestraRPCHttpServer.getInstance().addServlet(ColtRemoteServiceServlet.getInstance(), "/coltService");

        primaryStage.hide();

        primaryStage = mainStage;
        GAController.getInstance().start(primaryStage);
        primaryStage.show();

        // Open most recent project
        for (String recentProjectPath : RecentProjects.getRecentProjectsPaths()) {
            File projectFile = new File(recentProjectPath);
            if (projectFile.exists()) {
                try {
                    ColtProjectManager.getInstance().load(projectFile.getPath());
                    break;
                } catch (ColtException e) {
                    // ignore
                }
            }
        }

//        ScenicView.show(mainStage.getScene());
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

    public ColtMenuBar getMenuBar() {
        return menuBar;
    }


}
