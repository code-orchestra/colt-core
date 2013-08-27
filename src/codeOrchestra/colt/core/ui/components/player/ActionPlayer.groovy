package codeOrchestra.colt.core.ui.components.player

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.Separator
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.AnchorPane

/**
 * @author Dima Kruk
 */
class ActionPlayer extends AnchorPane {

    ToggleGroup toggleGroup
    @FXML ToggleButton pause
    @FXML ToggleButton play
    @FXML ToggleButton stop
    @FXML Separator separator
    @FXML Button add

    ActionPlayer() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("player_view.fxml"))
        fxmlLoader.root = this
        fxmlLoader.controller = this

        try {
            fxmlLoader.load()
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        toggleGroup = new ToggleGroup()
        toggleGroup.toggles.addAll(pause, play, stop)
        stop.selected = true
        pause.disable = true
//        pause.disableProperty().bind(pause.selectedProperty())
        play.disableProperty().bind(play.selectedProperty())
        stop.disableProperty().bind(stop.selectedProperty())

        showAdd(false)
    }

    void showAdd(boolean b) {
        separator.visible = b
        add.visible = add.managed = b
    }
}
