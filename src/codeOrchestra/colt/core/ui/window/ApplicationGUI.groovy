package codeOrchestra.colt.core.ui.window

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

    }

}
