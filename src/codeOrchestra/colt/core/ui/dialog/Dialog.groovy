package codeOrchestra.colt.core.ui.dialog

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window
import org.controlsfx.control.ButtonBar

/**
 * @author Dima Kruk
 */
class Dialog extends VBox {
    Stage stage

    protected HBox header
    protected Label label

    ButtonBar buttonBar
    Button ok_btn

    Dialog(Window owner) {
        VBox root = this
        stage = new Stage() {
            @Override public void showAndWait() {
                Window stageOwner = getOwner();
                if (stageOwner != null) {
                    // because Stage does not seem to centre itself over its owner, we
                    // do it here.
                    final double x = stageOwner.getX() + (stageOwner.getWidth() / 2.0) - (540/ 2.0);
                    final double y = stageOwner.getY() + (stageOwner.getHeight() / 2.0) - (root.prefHeight(-1)) / 2.0 - 50;
                    setX(x);
                    setY(y);
                }
                super.showAndWait();
            }
        }
        stage.initModality(Modality.WINDOW_MODAL)
        stage.initOwner(owner)

        initView()
    }

    void initView() {
        setMinHeight(120)
        setMinWidth(540)
        setPadding(new Insets(17, 30, 30, 30))

        header = new HBox()
        header.minHeight = Double.NEGATIVE_INFINITY
        label = new Label()
        label.setAlignment(Pos.TOP_LEFT);
        label.setTextAlignment(TextAlignment.LEFT);
        label.maxWidth = 540
        label.wrapText = true
        label.styleClass.add("h1")
        HBox.setMargin(label, new Insets(17, 0, 0, 0))
        header.children.add(label)

        buttonBar = new ButtonBar()
        ok_btn = new Button("OK")
        ok_btn.onAction = {
            stage.hide()
        } as EventHandler
        ok_btn.defaultButton = true
        ButtonBar.setType(ok_btn, ButtonBar.ButtonType.OK_DONE)
        buttonBar.buttons.add(ok_btn)
        setMargin(buttonBar, new Insets(10, 0, 0, 0))

        children.addAll(header, buttonBar)
    }

    void setMessage(String message) {
        label.text = message
    }

    void setTitle(String title) {
//        stage.title = title
    }

    void show() {
        stage.scene = new Scene(this)
        stage.showAndWait()
    }
}
