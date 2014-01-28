package codeOrchestra.colt.core.update.ui

import codeOrchestra.colt.core.net.ProxyDialog
import codeOrchestra.colt.core.ui.components.inputForms.LabeledActionInput
import codeOrchestra.colt.core.ui.components.inputForms.LabeledTitledInput
import codeOrchestra.colt.core.ui.components.inputForms.base.BrowseType
import codeOrchestra.colt.core.ui.components.inputForms.group.FormGroup
import codeOrchestra.colt.core.ui.components.scrollpane.SettingsScrollPane
import codeOrchestra.util.PathUtils
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.stage.Window

/**
 * @author Dima Kruk
 */
class UpdateView extends SettingsScrollPane {

    LabeledTitledInput url
    LabeledActionInput to

    UpdateView() {
        FormGroup formGroup = new FormGroup(first: true)
        mainContainer.children.add(formGroup)
        formGroup.children.addAll(
                url = new LabeledTitledInput(title: "Url", text: "http://codeorchestra.s3.amazonaws.com/flex_sdk.zip"),
                to = new LabeledActionInput(title: "To folder", text: PathUtils.applicationBaseDir.path + File.separator + "tmp", browseType: BrowseType.DIRECTORY)
        )

        Button action = new Button("Action")
        formGroup.children.add(action)

        action.onAction = {
            new ProxyDialog(parent as Window).show()
//            new InstallGradleDialog(parent as Window).show()
//            TasksManager.getInstance().scheduleBackgroundTask(new UpdateTask("http://codeorchestra.s3.amazonaws.com/flex_sdk.zip", to.text))
        } as EventHandler
    }
}
