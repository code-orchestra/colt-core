package codeOrchestra.colt.core.update.ui

import codeOrchestra.colt.core.tasks.TasksManager
import codeOrchestra.colt.core.ui.components.inputForms.LabeledActionInput
import codeOrchestra.colt.core.ui.components.inputForms.LabeledTitledInput
import codeOrchestra.colt.core.ui.components.inputForms.base.BrowseType
import codeOrchestra.colt.core.ui.components.inputForms.group.FormGroup
import codeOrchestra.colt.core.ui.components.scrollpane.SettingsScrollPane
import codeOrchestra.colt.core.update.tasks.UpdateTask
import codeOrchestra.util.PathUtils
import javafx.event.EventHandler
import javafx.scene.control.Button

/**
 * @author Dima Kruk
 */
class UpdateView extends SettingsScrollPane {
    private static final int BUFFER_SIZE = 4096;

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
            TasksManager.getInstance().scheduleBackgroundTask(new UpdateTask("http://codeorchestra.s3.amazonaws.com/flex_sdk.zip", to.text))
        } as EventHandler
    }
}
