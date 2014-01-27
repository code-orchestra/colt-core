package codeOrchestra.colt.core.ui.dialog

import codeOrchestra.colt.core.ui.components.inputForms.LabeledActionInput
import codeOrchestra.colt.core.ui.components.inputForms.base.BrowseType
import codeOrchestra.colt.core.update.tasks.UpdateTask
import codeOrchestra.util.PathUtils
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.scene.layout.VBox
import javafx.stage.Window

import java.util.prefs.Preferences

/**
 * @author Dima Kruk
 */
class InstallGradleDialog extends UpdateDialog {
    VBox pathCenter

    InstallGradleDialog(Window owner) {
        super(owner)
        task = new UpdateTask("http://codeorchestra.s3.amazonaws.com/colt_packages/gradle.zip",
                new File(PathUtils.getApplicationBaseDir(), "gradle").getPath())
    }

    @Override
    protected void initHeader() {
        super.initHeader()
        message = 'Please install or specify path to Gradle before using "Production build" or "Precompile"'
        comment = ""
    }

    @Override
    protected void initCenter() {
        super.initCenter()

        pathCenter = new VBox(padding: new Insets(13, -10, 4, 58))
        println "PathUtils.checkGradle() = ${PathUtils.checkGradle()}"
        LabeledActionInput gradlePaht = new LabeledActionInput(title: "Path to Gradle", browseType: BrowseType.DIRECTORY)
        gradlePaht.text().addListener({ ObservableValue<? extends String> observableValue, String t, String t1 ->
            if (t1.isEmpty()) {
                return
            }
            File file = new File(t1 + File.separator + "bin")
            if (file.exists() && (new File(file, "gradle").exists() || new File(file, "gradle.bat").exists())) {
                gradlePaht.error = false
                okButton.text = "OK"
                inited = true
                Preferences preferences = Preferences.userNodeForPackage(PathUtils.class)
                preferences.put("gradle.home", t1)
                preferences.sync()
            } else {
                gradlePaht.error = true
            }
        } as ChangeListener)
        pathCenter.children.add(gradlePaht)

        children.add(pathCenter)
    }

    @Override
    protected void initButtons() {
        super.initButtons()
        okButton.text = "Install"
    }

    @Override
    protected void startUpdate() {
        children.remove(pathCenter)
        super.startUpdate()
    }

    @Override
    protected void updateComplete() {
        super.updateComplete()
        okButton.text = "Done"
    }
}
