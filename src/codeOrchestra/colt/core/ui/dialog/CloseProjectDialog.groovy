package codeOrchestra.colt.core.ui.dialog

import javafx.event.ActionEvent
import javafx.stage.Stage
import javafx.stage.Window
import org.controlsfx.control.ButtonBar
import org.controlsfx.control.action.AbstractAction
import org.controlsfx.control.action.Action
import org.controlsfx.dialog.Dialog

/**
 * @author Dima Kruk
 */
class CloseProjectDialog {

    static public Action SAVE
    static public Action DONT_SAVE

    static Action show(Window stage) {
        SAVE = new AbstractAction("Save") {

            @Override
            public void execute(ActionEvent actionEvent) {
                Dialog dlg = (Dialog) actionEvent.getSource();
                dlg.hide();
            }
        }
        ButtonBar.setType(SAVE, ButtonBar.ButtonType.OK_DONE);

        DONT_SAVE = new AbstractAction("Don't Save") {

            @Override
            public void execute(ActionEvent actionEvent) {
                Dialog dlg = (Dialog) actionEvent.getSource();
                dlg.hide();
            }
        }
        ButtonBar.setType(DONT_SAVE, ButtonBar.ButtonType.LEFT);

        Dialog dlg = new Dialog(stage, "Do you want to save the changed you made?", false, true)
        dlg.setContent("Your changes will be lost if you don't save them")
        dlg.actions.addAll(SAVE, DONT_SAVE, Dialog.Actions.CANCEL)
        return dlg.show()
    }
}
