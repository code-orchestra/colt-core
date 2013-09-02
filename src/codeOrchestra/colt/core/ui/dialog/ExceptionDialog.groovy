package codeOrchestra.colt.core.ui.dialog

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window


/**
 * @author Dima Kruk
 */
class ExceptionDialog extends VBox {
    private static final String HIDE_STYLE = "btn-hide-dialog"
    private static final String SHOW_STYLE = "btn-show"

    @FXML ImageView imageView
    @FXML Label label

    @FXML AnchorPane details
    @FXML TextArea textArea
    boolean showDetails

    @FXML Button details_btn
    @FXML Button ok_btn

    Stage stage

    ExceptionDialog(Window owner) {
        super()

        FXMLLoader fxmlLoader = new FXMLLoader(ExceptionDialog.class.getResource("exception_dialog.fxml"))
        fxmlLoader.root = this
        fxmlLoader.controller = this
        try {
            fxmlLoader.load()
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        stage = new Stage()
        stage.title = "Error"
        stage.initModality(Modality.WINDOW_MODAL)
        stage.initOwner(owner)

        init()
    }

    void initException(Throwable exception, String massage = null) {

        label.text = massage == null ? exception.message : massage

        StringWriter sw = new StringWriter()
        PrintWriter pw = new PrintWriter(sw)
        exception.printStackTrace(pw)
        textArea.text = sw.toString()
    }

    private void init() {
        imageView.image = new Image("/codeOrchestra/colt/core/ui/style/images/messages/error-48x48.png")

        showDetails = true

        details_btn.onAction = {
            showDetails = !showDetails
        } as EventHandler

            ok_btn.onAction = {
                stage.hide()
            } as EventHandler
    }

    void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails
        if (showDetails) {
            details_btn.styleClass.remove(SHOW_STYLE)
            details_btn.styleClass.add(HIDE_STYLE)
            details_btn.text = "Hide details"
        } else {
            details_btn.styleClass.remove(HIDE_STYLE)
            details_btn.styleClass.add(SHOW_STYLE)
            details_btn.text = "Show details"
        }
        details.visible = details.managed = showDetails
        stage.sizeToScene()
    }

    void show() {
        stage.scene = new Scene(this)

        stage.showAndWait()
    }

}
