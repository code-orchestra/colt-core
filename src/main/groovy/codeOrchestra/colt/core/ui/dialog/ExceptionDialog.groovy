package codeOrchestra.colt.core.ui.dialog

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import javafx.stage.Window
import org.controlsfx.control.ButtonBar


/**
 * @author Dima Kruk
 */
class ExceptionDialog extends DialogWithImage {
    private static final String HIDE_STYLE = "btn-hide-dialog"
    private static final String SHOW_STYLE = "btn-show"

    TextArea textArea
    boolean showDetails

    Button details_btn

    ExceptionDialog(Window owner) {
        super(owner)
    }

    @Override
    protected void initView() {
        super.initView()

        Dialog.setTitle = "Error"
        image = new Image("/codeOrchestra/colt/core/ui/style/images/messages/error-48x48.png")

        setShowDetails(false)
    }

    @Override
    protected void initCenter() {
        textArea = new TextArea(prefHeight: 200,  wrapText: true)
//        textArea.editable = false
        textArea.stylesheets.add("/codeOrchestra/colt/core/ui/style/main.css")
        setMargin(textArea, new Insets(17, 0, 0, 0))
        setVgrow(textArea, Priority.ALWAYS)

        children.add(textArea)
    }

    @Override
    protected void initButtons() {
        super.initButtons()

        details_btn = new Button(focusTraversable: false)
        ButtonBar.setType(details_btn, ButtonBar.ButtonType.LEFT)
        buttonBar.buttons.add(details_btn)
        details_btn.onAction = {
            showDetails = !showDetails
        } as EventHandler
    }

    @Override
    protected void onCancel() {
        stage.hide()
    }

    void initException(Throwable exception, String massage = null) {
        if (massage) {
            this.message = massage
            comment = exception.message
        } else {
            this.message = exception.message
        }

        StringWriter sw = new StringWriter()
        PrintWriter pw = new PrintWriter(sw)
        exception.printStackTrace(pw)
        textArea.text = sw.toString()
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
        textArea.visible = textArea.managed = showDetails
        stage.sizeToScene()
    }

}
