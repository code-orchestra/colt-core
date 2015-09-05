package codeOrchestra.colt.core.net

import codeOrchestra.colt.core.ui.dialog.Dialog
import javafx.event.EventHandler
import javafx.stage.Window

/**
 * @author Dima Kruk
 */
class ProxyDialog extends Dialog {

    ProxyDialog(Window owner) {
        super(owner)
    }

    @Override
    protected void initCenter() {
        message = "Proxy settings"
        ProxySettingsView view = new ProxySettingsView()
        children.add(view)
    }

    @Override
    protected void initButtons() {
        super.initButtons()
        okButton.onAction = {
            ProxyModel.instance.save()
            stage.hide()
        } as EventHandler
    }
}
