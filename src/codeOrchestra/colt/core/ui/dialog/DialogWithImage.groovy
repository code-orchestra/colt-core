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
class DialogWithImage extends Dialog{

    ImageView imageView

    DialogWithImage(Window owner) {
        super(owner)
    }

    @Override
    protected void initView() {
        super.initView()

        imageView = new ImageView(fitHeight: 48, fitWidth: 48, preserveRatio: true, smooth: false)

        label.maxWidth = 470
        commentLabel.maxWidth = 470

        header.children.clear()
        header.spacing = 21
        header.children.addAll(imageView, messageContainer)
    }

    void setImage(Image image) {
        imageView.image = image
    }
}
