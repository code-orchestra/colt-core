package codeOrchestra.colt.core.ui.dialog

import codeOrchestra.colt.core.ColtException
import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.errorhandling.ErrorHandler
import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.control.Button
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
    void initView() {
        super.initView()

        message = 'Save changes to COLT "' + ColtProjectManager.instance.currentProject.name + '" project before closing?'

        ok_btn.text = "Save"
        ok_btn.onAction = {
            try {
                ColtProjectManager.getInstance().save();
            } catch (ColtException e) {
                ErrorHandler.handle(e, "Can't save project");
            }
            hide()
        } as EventHandler

        Button cancel = new Button("Cancel")
        ButtonBar.setType(cancel, ButtonBar.ButtonType.CANCEL_CLOSE)
        cancel.onAction = {
            flowEvent.consume()
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
                flowEvent.consume()
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
