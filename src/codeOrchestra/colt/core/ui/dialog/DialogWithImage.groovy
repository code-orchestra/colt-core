package codeOrchestra.colt.core.ui.dialog

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window

/**
 * @author Dima Kruk
 */
class DialogWithImage extends VBox{

    @FXML ImageView imageView
    @FXML Label label

    @FXML Button ok_btn

    Stage stage

    DialogWithImage(Window owner) {
        super()

        FXMLLoader fxmlLoader = new FXMLLoader(ExceptionDialog.class.getResource("dialog_with_image.fxml"))
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

    void init() {
        ok_btn.onAction = {
            stage.hide()
        } as EventHandler
    }

    void setImage(Image image) {
        imageView.image = image
    }

    void setTitle(String title) {
        stage.title = title
    }

    void setMessage(String message) {
        label.text = message
    }

    void show() {
        stage.scene = new Scene(this)

        stage.showAndWait()
    }
}
