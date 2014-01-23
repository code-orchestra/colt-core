package codeOrchestra.colt.core.ui.dialog

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.stage.Window
import org.controlsfx.control.ButtonBar

/**
 * @author Dima Kruk
 */
class UpdateDialog extends DialogWithImage {
    private HBox progressCenter

    boolean isSuccess = false

    UpdateDialog(Window owner) {
        super(owner)
    }

    @Override
    protected void initView() {
        super.initView()

        image = new Image("/codeOrchestra/colt/core/ui/style/images/messages/info-48x48.png")
    }

    @Override
    protected void initHeader() {
        super.initHeader()

        message = "You need update"
        comment = "comment"
    }

    @Override
    protected void initCenter() {
        progressCenter = new HBox(spacing: 8, padding: new Insets(2, 0, 4, 68))

        ProgressBar progressBar = new ProgressBar()
        progressBar.prefWidth = 416

        progressCenter.children.add(progressBar)
    }

    @Override
    protected void initButtons() {
        super.initButtons()

        okButton.text = "Update"
        okButton.onAction = {
            okButton.disable = true
            startUpdate()
        } as EventHandler

        Button cancel = new Button("Cancel")
        cancel.prefWidth = 67
        ButtonBar.setType(cancel, ButtonBar.ButtonType.CANCEL_CLOSE)
        cancel.onAction = {
            cancelUpdate()
            stage.hide()
        } as EventHandler

        buttonBar.buttons.add(cancel)

        stage.onCloseRequest = {
            cancelUpdate()
        } as EventHandler
    }

    protected void startUpdate() {
        children.add(1, progressCenter)
        stage.sizeToScene()
    }

    protected void cancelUpdate() {

    }
}
