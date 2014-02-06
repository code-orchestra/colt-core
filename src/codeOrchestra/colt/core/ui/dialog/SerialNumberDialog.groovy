package codeOrchestra.colt.core.ui.dialog

import codeOrchestra.colt.core.LiveCodingManager
import codeOrchestra.colt.core.annotation.Service
import codeOrchestra.colt.core.license.DemoHelper
import codeOrchestra.colt.core.net.ProxyModel
import codeOrchestra.colt.core.net.ProxySettingsView
import codeOrchestra.colt.core.ui.components.advancedSeparator.AdvancedSeparator
import codeOrchestra.colt.core.ui.components.inputForms.group.FormGroup
import javafx.beans.InvalidationListener
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Window

import java.awt.Desktop

/**
 * @author Dima Kruk
 */
class SerialNumberDialog extends DialogWithImage {
    private HBox center

    Label inputLabel
    TextField serialNumber
    Image errorImage = new Image("/codeOrchestra/colt/core/ui/style/images/messages/error-48x48.png")

    String serialNumberValue

    AdvancedSeparator separator
    private Button demo

    protected @Service LiveCodingManager liveCodingManager

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
        comment = "COLT is in Demo mode. AS compilations count is limited to 25, JS updates to 25"
    }

    @Override
    protected void initCenter() {
        center = new HBox(spacing: 8, padding: new Insets(22, 0, 24, 68))
        Button purchase = new Button(text: "Purchase", prefWidth: 204, focusTraversable: false)
        purchase.onAction = {
            Desktop.getDesktop().browse(new URI("https://www.plimus.com/jsp/buynow.jsp?contractId=3190926"));
        } as EventHandler
        demo = new Button(text: "Continue With Demo", prefWidth: 204, focusTraversable: false)
        demo.onAction = {
            hide()
            serialNumberValue = null
        } as EventHandler
        center.children.addAll(purchase, demo)

        children.add(center)
    }

    @Override
    protected void initButtons() {
        AnchorPane pane = new AnchorPane()

        inputLabel = new Label("Or enter a serial number:")
        AnchorPane.setRightAnchor(inputLabel, 0)
        AnchorPane.setLeftAnchor(inputLabel, 69)

        serialNumber = new TextField(prefHeight: 30)
        AnchorPane.setTopAnchor(serialNumber, 18)
        AnchorPane.setRightAnchor(serialNumber, 86)
        AnchorPane.setLeftAnchor(serialNumber, 63)

        okButton = new Button(text: "OK", prefWidth: 67, defaultButton: true, focusTraversable: false)
        AnchorPane.setTopAnchor(okButton, 18)
        AnchorPane.setRightAnchor(okButton, 0)

        VBox vBox = new VBox()
        AnchorPane.setTopAnchor(vBox, 37)
        AnchorPane.setRightAnchor(vBox, 0)
        AnchorPane.setLeftAnchor(vBox, 63)
        separator = new AdvancedSeparator("Proxy settings", false)
        FormGroup proxySettings = new ProxySettingsView().proxySettings

        separator.content = proxySettings
        proxySettings.visibleProperty().addListener({ javafx.beans.Observable observable ->
            stage.sizeToScene()
        } as InvalidationListener)
        vBox.children.addAll(separator, proxySettings)

        pane.children.addAll(inputLabel, serialNumber, okButton, vBox)

        children.add(pane)

        okButton.onAction = {
            if (serialNumber.text.isEmpty()) {
                error("The serial number entered is empty")
            } else {
                ProxyModel.instance.save()
                serialNumberValue = serialNumber.text
                hide()
            }
        } as EventHandler
    }

    void hide() {
        stage.hide()
    }

    void error(String message, String comment = "") {
        this.message = message
        this.comment = comment

        if (children.contains(center)) {
            children.remove(center)
        }

        inputLabel.text = "Enter a serial number:"
        image = errorImage
        stage.sizeToScene()
    }

    void showRepeat() {
        demo.onAction = {
            stage.hide()
            liveCodingManager.flush()
            DemoHelper.get().reset()
        } as EventHandler
        show()
    }

    void showInput() {
        message = "Please type the serial number purchased"
        comment = ""
        inputLabel.text = ""
        children.remove(center)

        super.show()
    }
}
