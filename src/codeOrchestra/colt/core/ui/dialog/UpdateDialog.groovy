package codeOrchestra.colt.core.ui.dialog

import codeOrchestra.colt.core.update.tasks.UpdateTask
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Worker
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
    ProgressBar progressBar

    public boolean isSuccess = false

    UpdateTask task

    Button cancelButton

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

        progressBar = new ProgressBar()
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

        cancelButton = new Button("Cancel")
        cancelButton.prefWidth = 67
        ButtonBar.setType(cancelButton, ButtonBar.ButtonType.CANCEL_CLOSE)
        cancelButton.onAction = {
            cancelUpdate()
            stage.hide()
        } as EventHandler

        buttonBar.buttons.add(cancelButton)

        stage.onCloseRequest = {
            cancelUpdate()
        } as EventHandler
    }

    protected void startUpdate() {
        children.add(1, progressCenter)
        stage.sizeToScene()

        task.stateProperty().addListener({ ObservableValue<? extends Worker.State> observableValue, Worker.State t, Worker.State t1 ->
            switch (t1){
                case Worker.State.SUCCEEDED:
                    updateComplete()
                    break
                case Worker.State.CANCELLED:
                    break
                case Worker.State.FAILED:
                    break
            }
        } as ChangeListener)

        progressBar.progress = 0
        progressBar.progressProperty().unbind()
        progressBar.progressProperty().bind(task.progressProperty())
        new Thread(task).start()
    }

    protected void cancelUpdate() {
        if (task != null && task.running) {
            task.cancel()
        }
    }

    protected void updateComplete() {
        cancelButton.visible = false
        okButton.disable = false
        okButton.text = "Done"
    }
}
