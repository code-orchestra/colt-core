package codeOrchestra.colt.core.ui.components.player

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.Button
import javafx.scene.control.ButtonBase
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Separator
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane

/**
 * @author Dima Kruk
 */
class ActionPlayer extends AnchorPane {

    ToggleGroup toggleGroup
    ToggleButton pause
    ToggleButton play
    ToggleButton stop
    Separator separator
    Button add

    ActionPlayer() {

        StackPane stackPane = new StackPane(minHeight: Double.NEGATIVE_INFINITY, minWidth: Double.NEGATIVE_INFINITY, prefHeight: 94)
        stackPane.styleClass.add("player-box")
        HBox hBox = new HBox(spacing: 10, prefHeight: 58, minHeight: 58, maxHeight: 58, padding: new Insets(9, 0, 11, 3))
        hBox.children.addAll(
                pause = new ToggleButton(contentDisplay: ContentDisplay.GRAPHIC_ONLY, focusTraversable: false, prefHeight: 24, prefWidth: 20),
                play = new ToggleButton(contentDisplay: ContentDisplay.GRAPHIC_ONLY, focusTraversable: false, prefHeight: 24, prefWidth: 20),
                stop = new ToggleButton(contentDisplay: ContentDisplay.GRAPHIC_ONLY, focusTraversable: false, prefHeight: 24, prefWidth: 20),
                separator = new Separator(orientation: Orientation.VERTICAL, prefWidth: 1, prefHeight: 38, minHeight: Double.NEGATIVE_INFINITY),
                add = new Button(contentDisplay: ContentDisplay.GRAPHIC_ONLY, focusTraversable: false, prefHeight: 24, prefWidth: 20)
        )
        Insets insets = new Insets(7, 0, 0, 0)
        hBox.children.grep(ButtonBase).each {
            HBox.setMargin(it, insets)
        }
        pause.styleClass.add("btn-player-pause")
        play.styleClass.add("btn-player-play")
        stop.styleClass.add("btn-player-stop")
        add.styleClass.add("btn-player-add")
        stackPane.children.add(hBox)
        children.add(stackPane)

        toggleGroup = new ToggleGroup()
        toggleGroup.toggles.addAll(pause, play, stop)
        stop.selected = true
        pause.disable = true
//        pause.disableProperty().bind(pause.selectedProperty())
        play.disableProperty().bind(play.selectedProperty())
        stop.disableProperty().bind(stop.selectedProperty())

        stop.selectedProperty().addListener({ ObservableValue<? extends Boolean> observableValue, Boolean t, Boolean newValue ->
            if (newValue) {
                showAdd(false)
            }
        } as ChangeListener)

        showAdd(false)
    }

    void showAdd(boolean b) {
        separator.visible = separator.managed = b
        add.visible = add.managed = b
    }
}
