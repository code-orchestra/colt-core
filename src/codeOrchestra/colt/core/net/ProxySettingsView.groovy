package codeOrchestra.colt.core.net

import codeOrchestra.colt.core.ui.components.advancedSeparator.AdvancedSeparator
import codeOrchestra.colt.core.ui.components.inputForms.CheckBoxInput
import codeOrchestra.colt.core.ui.components.inputForms.LabeledPasswordInput
import codeOrchestra.colt.core.ui.components.inputForms.LabeledTitledInput
import codeOrchestra.colt.core.ui.components.inputForms.base.InputWithErrorBase
import codeOrchestra.colt.core.ui.components.inputForms.group.FormGroup
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.layout.VBox

/**
 * @author Dima Kruk
 */
class ProxySettingsView extends VBox {

    FormGroup proxySettings
    AdvancedSeparator separator

    ProxySettingsView() {
        proxySettings = new FormGroup(first: true)
        ProxyModel model = ProxyModel.instance
        CheckBoxInput checkBoxInput
        proxySettings.children.addAll(
                checkBoxInput = new CheckBoxInput(title: "Use Proxy", bindProperty: model.useProxy()),
                new LabeledTitledInput(title: "Host", bindProperty: model.host()),
                new LabeledTitledInput(title: "Port", bindProperty: model.port(), numeric: true),
                new LabeledTitledInput(title: "Username", bindProperty: model.username()),
                new LabeledPasswordInput(title: "Password", bindProperty: model.password())
        )

        checkBoxInput.selected().addListener({ ObservableValue<? extends Boolean> observableValue, Boolean t, Boolean t1 ->
            disableInputs(!t1)
        } as ChangeListener)

        disableInputs(!model.useProxy)

        children.add(proxySettings)
    }

    protected void disableInputs(boolean b) {
        proxySettings.children.each {
            if (it instanceof InputWithErrorBase) {
                it.disable = b
            }
        }
    }
}
