package codeOrchestra.colt.core.ui.dialog

import codeOrchestra.colt.core.ui.components.inputForms.FormType
import codeOrchestra.colt.core.ui.components.inputForms.LTBForm
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.stage.Window

/**
 * @author Dima Kruk
 */
class SerialNumberDialog extends DialogWithImage {
    private HBox center

    SerialNumberDialog(Window owner) {
        super(owner)
    }

    @Override
    protected void initView() {
        super.initView()

        image = new Image("/codeOrchestra/colt/core/ui/style/images/ico-colt.png")
    }

    @Override
    protected void initHeader() {
        super.initHeader()

        message = "This is a demo version of COLT"
        comment = "10 compilations have remained."
    }

    @Override
    protected void initCenter() {
        center = new HBox(spacing: 8, padding: new Insets(22, 0, 24, 68))
        Button purchase = new Button(text: "Purchase", prefWidth: 204, focusTraversable: false)
        Button demo = new Button(text: "Continue With Demo", prefWidth: 204, focusTraversable: false)
        center.children.addAll(purchase, demo)

        children.add(center)
    }

    @Override
    protected void initButtons() {
        AnchorPane pane = new AnchorPane()

        Label inputLabel = new Label("Or enter serial number:")
        AnchorPane.setRightAnchor(inputLabel, 0)
        AnchorPane.setLeftAnchor(inputLabel, 69)

        TextField textField = new TextField(prefHeight: 30)
        AnchorPane.setTopAnchor(textField, 18)
        AnchorPane.setRightAnchor(textField, 86)
        AnchorPane.setLeftAnchor(textField, 63)

        ok_btn = new Button(text: "OK", prefWidth: 67, defaultButton: true, focusTraversable: false)
        AnchorPane.setTopAnchor(ok_btn, 18)
        AnchorPane.setRightAnchor(ok_btn, 0)

        pane.children.addAll(inputLabel, textField, ok_btn)

        children.add(pane)

        ok_btn.onAction = {
            stage.hide()
        } as EventHandler


    }
}
