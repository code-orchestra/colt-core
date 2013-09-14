package codeOrchestra.colt.core.ui

import codeOrchestra.colt.core.LiveCodingManager
import codeOrchestra.colt.core.ServiceProvider
import codeOrchestra.colt.core.annotation.Service
import codeOrchestra.colt.core.logging.Level
import codeOrchestra.colt.core.rpc.security.ui.ShortCodeNotification
import codeOrchestra.colt.core.session.LiveCodingSession
import codeOrchestra.colt.core.session.listener.LiveCodingAdapter
import codeOrchestra.colt.core.session.listener.LiveCodingListener
import codeOrchestra.colt.core.ui.components.ProgressIndicatorController
import codeOrchestra.colt.core.ui.components.log.Log
import codeOrchestra.colt.core.ui.components.log.LogFilter
import codeOrchestra.colt.core.ui.components.log.LogMessage
import codeOrchestra.colt.core.ui.components.log.LogWebView
import codeOrchestra.colt.core.ui.components.player.ActionPlayerPopup
import codeOrchestra.colt.core.ui.components.popupmenu.PopupMenu
import codeOrchestra.colt.core.ui.components.sessionIndicator.SessionIndicatorController
import codeOrchestra.colt.core.ui.groovy.GroovyDynamicMethods
import codeOrchestra.groovyfx.FXBindable
import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.binding.StringBinding
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.text.TextAlignment

import static java.lang.Double.*
import static javafx.scene.control.ContentDisplay.*

/**
 * @author Dima Kruk
 */
abstract class ApplicationGUI extends BorderPane {

    static {
        GroovyDynamicMethods.init()
    }

    protected Label projectTitle
    protected Label projectType

    protected BorderPane root

    protected ActionPlayerPopup actionPlayerPopup

    protected ToggleGroup navigationToggleGroup = new ToggleGroup()

    protected ToggleButton runButton
    protected ToggleButton buildButton
    protected ToggleButton settingsButton

    protected Button popupMenuButton

    protected HBox logFiltersContainer

    protected ToggleGroup logFilterToggleGroup = new ToggleGroup()
    protected ToggleButton logFilterAll
    protected ToggleButton logFilterErrors
    protected ToggleButton logFilterWarnings
    protected ToggleButton logFilterInfo
    protected ToggleButton logFilterLog

    protected ImageView sessionIndicator
    protected ProgressIndicator progressIndicator

    @Lazy LogWebView logView = Log.instance.logWebView

    List<ToggleButton> allFilters

    @FXBindable String applicationState = ""

    private @Service LiveCodingManager liveCodingManager

    boolean isFirstTime = true

    private LiveCodingListener liveCodingListener = new LiveCodingAdapter() {
        @Override
        void onSessionStart(LiveCodingSession session) {
            Platform.runLater({
                actionPlayerPopup.actionPlayer.showAdd(true)
                actionPlayerPopup.actionPlayer.disable = false
            })
        }

        @Override
        void onSessionEnd(LiveCodingSession session) {
            if (liveCodingManager.currentConnections.size() == 0) {
                Platform.runLater{
                    actionPlayerPopup.actionPlayer.stop.selected = true
                    actionPlayerPopup.actionPlayer.disable = false
                }
            }
        }
    }

    ApplicationGUI() {
        VBox sidebar; Pane leftPane

        setCenter(root = new BorderPane(
                top: new HBox(alignment: Pos.CENTER, prefHeight: -1.0, prefWidth: 200.0, newStyleClass: "title-bar", newChildren: [
                        projectTitle = new Label(ellipsisString: "…", textAlignment: TextAlignment.CENTER)
                ]),
                bottom: new HBox(alignment: Pos.CENTER_RIGHT, prefHeight: -1.0, prefWidth: -1.0, spacing: 5.0, newStyleClass: "status-bar",
                        newChildren: [
                                logFiltersContainer = new HBox(prefHeight:-1.0, prefWidth:-1.0, newStyleClass:"filters", newChildren: [
                                        logFilterAll = new ToggleButton(mnemonicParsing: false, selected: true, text: "All", minWidth: NEGATIVE_INFINITY),
                                        logFilterErrors = new ToggleButton(mnemonicParsing: false, selected: false, text: "Errors", minWidth: NEGATIVE_INFINITY),
                                        logFilterWarnings = new ToggleButton(mnemonicParsing: false, selected: false, text: "Warnings", minWidth: NEGATIVE_INFINITY),
                                        logFilterInfo = new ToggleButton(mnemonicParsing: false, selected: false, text: "Info", minWidth: NEGATIVE_INFINITY),
                                        logFilterLog = new ToggleButton(mnemonicParsing: false, selected: false, text: "Log", minWidth: NEGATIVE_INFINITY)
                                ]),
                                new AnchorPane(prefWidth: -1.0, newChildren: [
                                        sessionIndicator = new ImageView(fitHeight: 13.0, fitWidth: 13.0, layoutX: 1.0, layoutY: 3.0, pickOnBounds: true, preserveRatio: true),
                                        progressIndicator = new ProgressIndicator(layoutX: 0.0, layoutY: 2.0, maxHeight: NEGATIVE_INFINITY, maxWidth: NEGATIVE_INFINITY, prefHeight: 15.0, prefWidth: 15.0, visible: false)
                                ]),
                                projectType = new Label()
                        ]
                )
        ))

        setLeft(sidebar = new VBox(
                runButton = new ToggleButton(contentDisplay: GRAPHIC_ONLY, focusTraversable: false, maxWidth: 1.7976931348623157E308, mnemonicParsing: false, prefHeight: 40.0, prefWidth: 60.0, selected: false, text: "Run", newStyleClass: "btn-run"),
                buildButton = new ToggleButton(contentDisplay: GRAPHIC_ONLY, focusTraversable: false, maxWidth: 1.7976931348623157E308, mnemonicParsing: false, prefHeight: 40.0, prefWidth: 60.0, selected: false, text: "Build", newStyleClass: "btn-build"),
                settingsButton = new ToggleButton(contentDisplay: GRAPHIC_ONLY, focusTraversable: false, maxWidth: 1.7976931348623157E308, mnemonicParsing: false, prefHeight: 40.0, prefWidth: 60.0, selected: false, text: "Settings", newStyleClass: "btn-settings"),
                leftPane = new Pane(maxHeight: 1.7976931348623157E308),
                popupMenuButton = new Button(contentDisplay: GRAPHIC_ONLY, focusTraversable: false, maxWidth: 1.7976931348623157E308, mnemonicParsing: false, prefHeight: 40.0, prefWidth: 60.0, text: "Menu", newStyleClass: "btn-more")

        ))

        HBox.setHgrow(logFiltersContainer, Priority.ALWAYS)
        VBox.setVgrow(leftPane, Priority.ALWAYS)
        sidebar.maxWidth = NEGATIVE_INFINITY
        sidebar.styleClass.add("sidebar")

        root.stylesheets.add("/codeOrchestra/colt/core/ui/style/main.css")

        initLog(); init()
    }

    LiveCodingManager get

    private void init() {
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

        PopupMenu popupMenu = new PopupMenu()
        ArrayList<MenuItem> items = ColtApplication.get().menuBar.popupMenuItems
        items.each {
            popupMenu.menuContent.add(it)
            popupMenu.contextMenu.items.add(it)
        }

        popupMenuButton.onAction = {
            popupMenu.isShowing() ? popupMenu.hide() : popupMenu.show(popupMenuButton)
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
            allFilters.each { it.visible = root.center == logView }
            updateLogFilter()
        } as ChangeListener)

        logFiltersContainer.widthProperty().addListener({ o, old, Number newValue ->
            updateLogFilter()
        } as ChangeListener)

        liveCodingManager.addListener(liveCodingListener)
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
        logFilterErrors.text = "Errors" + logFilterPrefix(Level.ERROR)
        logFilterWarnings.text = "Warnings" + logFilterPrefix(Level.WARN)
        logFilterInfo.text = "Info" + logFilterPrefix(Level.INFO)
        logFilterLog.text = "Log" + logFilterPrefix(Level.COMPILATION, Level.LIVE)

        if (!logFilterToggleGroup.selectedToggle) {
            logFilterAll.selected = true
            return
        }

        int filterIndex = allFilters.indexOf(logFilterToggleGroup.selectedToggle)
        logView.filter(LogFilter.values()[filterIndex])
    }

    private String logFilterPrefix(Level... levels) {
        if (logView.logMessages.empty || logFiltersContainer.width < 300) return ""
        " (" + logView.logMessages.grep { LogMessage m -> m.level in levels }.size() + ")"
    }

    abstract protected void initLog();

    abstract protected void initGoogleAnalytics();
}
