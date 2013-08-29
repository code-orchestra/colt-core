package codeOrchestra.colt.core.ui

import codeOrchestra.colt.core.logging.Level
import codeOrchestra.colt.core.rpc.security.ui.ShortCodeNotification
import codeOrchestra.colt.core.ui.components.ProgressIndicatorController
import codeOrchestra.colt.core.ui.components.log.Log
import codeOrchestra.colt.core.ui.components.log.LogFilter
import codeOrchestra.colt.core.ui.components.log.LogMessage
import codeOrchestra.colt.core.ui.components.log.LogWebView
import codeOrchestra.colt.core.ui.components.player.ActionPlayerPopup
import codeOrchestra.colt.core.ui.components.popupmenu.MyContextMenu
import codeOrchestra.colt.core.ui.components.sessionIndicator.SessionIndicatorController
import codeOrchestra.groovyfx.FXBindable
import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.binding.StringBinding
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox

/**
 * @author Dima Kruk
 */
abstract class ApplicationGUI extends BorderPane{

    @FXML protected Label projectTitle
    @FXML protected Label projectType

    @FXML protected BorderPane root

    protected ActionPlayerPopup actionPlayerPopup

    protected ToggleGroup navigationToggleGroup = new ToggleGroup()
    @FXML protected ToggleButton runButton
    @FXML protected ToggleButton buildButton
    @FXML protected ToggleButton settingsButton

    @FXML protected Button popupMenuButton

    @FXML protected HBox logFiltersContainer

    protected ToggleGroup logFilterToggleGroup = new ToggleGroup()
    @FXML protected ToggleButton logFilterAll
    @FXML protected ToggleButton logFilterErrors
    @FXML protected ToggleButton logFilterWarnings
    @FXML protected ToggleButton logFilterInfo
    @FXML protected ToggleButton logFilterLog

    @FXML protected ProgressIndicator progressIndicator
    @FXML protected ImageView sessionIndicator

    @Lazy LogWebView logView = Log.instance.logWebView
    List<ToggleButton> allFilters

    @FXBindable String applicationState

    ApplicationGUI() {
        println "ApplicationGUI"
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationGUI.class.getResource("main_gui.fxml"))
        fxmlLoader.root = this
        fxmlLoader.controller = this

        try {
            fxmlLoader.load()
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        initLog();
        Platform.runLater{initialize()}
    }

    protected void initialize() {
        initGoogleAnalytics()

        // build ui

        root.top = ShortCodeNotification.initNotification(root.top)

        initActionPlayerPopup()

        navigationToggleGroup.toggles.addAll(runButton, buildButton, settingsButton)

        allFilters = [logFilterAll, logFilterErrors, logFilterWarnings, logFilterInfo, logFilterLog]
        logFilterToggleGroup.toggles.addAll(allFilters)

        // progress monitor

        ProgressIndicatorController.instance.progressIndicator = progressIndicator

        sessionIndicator.visibleProperty().bind(progressIndicator.visibleProperty().not())
        SessionIndicatorController.instance.indicator = sessionIndicator

        MyContextMenu contextMenu = new MyContextMenu()
        contextMenu.setStyle("-fx-background-color: transparent;");
        ArrayList<MenuItem> items = ColtApplication.get().menuBar.popupMenuItems
        contextMenu.items.addAll(items)

        popupMenuButton.onAction = {
            Point2D point = popupMenuButton.parent.localToScreen(popupMenuButton.layoutX, popupMenuButton.layoutY)
            contextMenu.show(popupMenuButton, point.x + 5, point.y - 15 - items.size() * 25)
        } as EventHandler

        // data binding

        navigationToggleGroup.selectedToggleProperty().addListener({ v, o, newValue ->
            int index = navigationToggleGroup.toggles.indexOf(navigationToggleGroup.selectedToggle)
            applicationState = ["Log", "Production Build", "Project Settings"][index]
        } as ChangeListener)

        logView.logMessages.addListener({ ListChangeListener.Change<? extends LogMessage> c ->
            updateLogFilter()
        } as ListChangeListener)

        logFilterToggleGroup.selectedToggleProperty().addListener({ o ->
            updateLogFilter()
        } as InvalidationListener)

        root.centerProperty().addListener({ o, old, javafx.scene.Node newValue ->
            allFilters.each {it.visible = root.center == logView }
            updateLogFilter()
        } as ChangeListener)

        logFiltersContainer.widthProperty().addListener({ o, old, Number newValue ->
            updateLogFilter()
        } as ChangeListener)

    }

    protected bindTitle(StringProperty value) {
        projectTitle.textProperty().bind(new StringBinding() {
            {
                super.bind(value, applicationState())
            }
            @Override
            protected String computeValue() {
                value.get().capitalize() + " / " + getApplicationState()
            }
        })
    }

    protected initActionPlayerPopup() {
        actionPlayerPopup = new ActionPlayerPopup()
    }

    protected void updateLogFilter() {
        if (!logFilterToggleGroup.selectedToggle) {
            logFilterAll.selected = true
            return
        }

        int filterIndex = allFilters.indexOf(logFilterToggleGroup.selectedToggle)
        logView.filter(LogFilter.values()[filterIndex])
        logFilterErrors.text = "Errors" + logFilterPrefix(Level.ERROR)
        logFilterWarnings.text = "Warnings" + logFilterPrefix(Level.WARN)
        logFilterInfo.text = "Info" + logFilterPrefix(Level.INFO)
        logFilterLog.text = "Log" + logFilterPrefix(Level.COMPILATION, Level.LIVE)
    }

    private String logFilterPrefix(Level... levels) {
        if(logView.logMessages.empty || logFiltersContainer.width < 300) return  ""
        " (" + logView.logMessages.grep { LogMessage m -> m.level in levels }.size() + ")"
    }

    abstract protected void initLog();

    abstract protected void initGoogleAnalytics();

}
