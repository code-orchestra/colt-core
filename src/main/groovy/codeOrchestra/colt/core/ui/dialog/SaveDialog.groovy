package codeOrchestra.colt.core.ui.dialog

import codeOrchestra.colt.core.ColtException
import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.errorhandling.ErrorHandler
import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.stage.Window
import org.controlsfx.control.ButtonBar


/**
 * @author Dima Kruk
 */
class SaveDialog extends DialogWithImage {
    Event flowEvent
    boolean action

    SaveDialog(Window owner) {
        super(owner)
    }

    @Override
    protected void initView() {
        super.initView()

        image = new Image("/codeOrchestra/colt/core/ui/style/images/messages/warning-48x48.png")

        message = 'Save changes to COLT "' + ColtProjectManager.instance.currentProject?.name + '" project before closing?'

        okButton.text = "Save"
        okButton.onAction = {
            try {
                ColtProjectManager.getInstance().save();
            } catch (ColtException e) {
                ErrorHandler.handle(e, "Can't save project");
            }
            hide()
        } as EventHandler

        Button cancel = new Button("Cancel")
        cancel.prefWidth = 67
        ButtonBar.setType(cancel, ButtonBar.ButtonType.CANCEL_CLOSE)
        cancel.onAction = {
            flowEvent?.consume()
            hide()
        } as EventHandler

        Button dontSave = new Button("Don't Save")
        ButtonBar.setType(dontSave, ButtonBar.ButtonType.LEFT)
        dontSave.onAction = {
            hide()
        } as EventHandler

        buttonBar.buttons.addAll(cancel, dontSave)

        stage.onCloseRequest = {
            if(!action){
                flowEvent?.consume()
            }
        } as EventHandler

    }

    void show(Event event) {
        flowEvent = event
        super.show()
    }

    void hide() {
        action = true
        stage.hide()
    }
}
