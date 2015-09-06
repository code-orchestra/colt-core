package codeOrchestra.colt.core.ui.components.popupmenu
import com.sun.javafx.scene.control.skin.KeystrokeUtils
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.VBox

/**
 * @author Dima Kruk
 */
class PopupMenuContent extends Region {
    Pane background
    VBox itemsContainer

    PopupMenuContent() {
        background = new Pane()
        background.styleClass.add("popup-bl")
        children.add(background)

        itemsContainer = new VBox()
        itemsContainer.styleClass.add("menu-v")
        itemsContainer.layoutX = 17
        itemsContainer.layoutY = 16
        children.add(itemsContainer)

        background.prefWidthProperty().bind(itemsContainer.widthProperty().add(34))
        background.prefHeightProperty().bind(itemsContainer.heightProperty().add(42))
    }

    void add(MenuItem item) {
        if (item instanceof SeparatorMenuItem) {
            Separator separator = new Separator(Orientation.HORIZONTAL)
            separator.padding = new Insets(5, 0, 5, 0)
            itemsContainer.children.add(separator)
        } else {
            Button btn = new Button(prefWidth: 220,
                    prefHeight: 28,
                    contentDisplay: ContentDisplay.GRAPHIC_ONLY,
                    focusTraversable: false)

            btn.onAction = item.onAction
            btn.disableProperty().bind(item.disableProperty())

            AnchorPane anchorPane = new AnchorPane()

            Label text = new Label(item.text)
            AnchorPane.setLeftAnchor(text, 0)

            Label accelerator = new Label(KeystrokeUtils.toString(item.accelerator))
            AnchorPane.setRightAnchor(accelerator, 0)

            anchorPane.children.addAll(text, accelerator)

            btn.graphic = anchorPane

            itemsContainer.children.add(btn)
        }
    }

    void addAll(MenuItem... items) {
        items.each {add(it)}
    }

    @Override
    protected double computePrefHeight(double width) {
        double h = 31
        itemsContainer.children.each {
            if (it instanceof Separator) {
                h += 13
            } else {
                h += 28
            }
        }
        return h
    }
}
