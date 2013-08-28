package codeOrchestra.colt.core.ui.window

import codeOrchestra.colt.core.logging.Level
import codeOrchestra.colt.core.ui.components.log.Log
import codeOrchestra.colt.core.ui.components.log.LogFilter
import codeOrchestra.colt.core.ui.components.log.LogMessage
import codeOrchestra.colt.core.ui.components.log.LogWebView
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Label
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

    @FXML protected BorderPane root

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
        Platform.runLater({initialize()})
    }

    protected void initialize() {

        navigationToggleGroup.toggles.addAll(runButton, buildButton, settingsButton)

        allFilters = [logFilterAll, logFilterErrors, logFilterWarnings, logFilterInfo, logFilterLog]
        logFilterToggleGroup.toggles.addAll(allFilters)

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

}
