package codeOrchestra.colt.core.ui.dialog

import codeOrchestra.colt.core.ColtException
import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.RecentProjects
import codeOrchestra.colt.core.errorhandling.ErrorHandler
import codeOrchestra.colt.core.model.Project
import codeOrchestra.colt.core.tasks.ColtTaskWithProgress
import codeOrchestra.colt.core.tasks.TasksManager
import codeOrchestra.colt.core.ui.ColtApplication
import codeOrchestra.colt.core.ui.ProjectStage
import codeOrchestra.colt.core.ui.components.IProgressIndicator
import codeOrchestra.util.ApplicationUtil
import codeOrchestra.util.BrowserUtil
import codeOrchestra.util.PathUtils
import javafx.scene.Scene
import javafx.stage.FileChooser
import javafx.stage.WindowEvent

/**
 * @author Dima Kruk
 */
class ProjectDialogs {

    static void newAsProjectDialog(Scene scene, boolean load) {
        newProjectDialog(scene, "AS", load)
    }

    static void newJsProjectDialog(Scene scene, boolean load) {
        newProjectDialog(scene, "JS", load)
    }

    private static void newProjectDialog(Scene scene, String handlerId, boolean load) {
        FileChooser fileChooser = new FileChooser()
        fileChooser.extensionFilters.add(new FileChooser.ExtensionFilter("COLT", "*.colt"))
        File file = fileChooser.showSaveDialog(scene.window)
        if (file != null) {
            try {
                ColtProjectManager.instance.create(handlerId, file.name[0..-6], file, load)
                if (!load) {
                    RecentProjects.mustOpenRecentProject = true
                    RecentProjects.addRecentProject(file.path)
                    ApplicationUtil.startAnotherColtInstance()
                }
            } catch (ColtException e) {
                ErrorHandler.handle(e, "Can't create a new project")
            }
        }
    }

    static void openProjectDialog(Scene scene, boolean sameInstance = true, File initialDirectory = null) {
        FileChooser fileChooser = new FileChooser()
        fileChooser.initialDirectory = initialDirectory
        fileChooser.extensionFilters.add(new FileChooser.ExtensionFilter("COLT", "*.colt"))
        File file = fileChooser.showOpenDialog(scene.window)
        if (file != null) {
            try {
                if (sameInstance) {
                    ColtProjectManager.instance.load(file.getPath())
                } else {
                    RecentProjects.addRecentProject(file.path)
                    RecentProjects.mustOpenRecentProject = true
                    ApplicationUtil.restartColt()
                }
            } catch (ColtException e) {
                ErrorHandler.handle(e, "Can't load the project")
            }
        }
    }

    static void openDemoProjectDialog(Scene scene) {
        File examplesDir = PathUtils.examplesDir
        if (examplesDir == null || !examplesDir.exists()) {
            ErrorHandler.handle("Can't locate the example projects folder");
        } else {
            openProjectDialog(scene, true, examplesDir)
        }
    }

    static void openWelcomeScreen(Scene scene) {
        closeProjectDialog()//todo: или оставить текущий проект?
    }

    static void closeProjectDialog() {
        ProjectStage mainStage = ColtApplication.get().mainStage
        WindowEvent event = new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST)
        if (mainStage.isShowing()) {
            mainStage.fireEvent(event)
        }
        if (mainStage.disposed) {
            RecentProjects.mustOpenRecentProject = false
            ApplicationUtil.restartColt()
        }
    }

    static void saveProjectDialog() {
        TasksManager.getInstance().scheduleBackgroundTask(new ColtTaskWithProgress() {
            @Override
            protected Object call(IProgressIndicator progressIndicator) {
                try {
                    ColtProjectManager.getInstance().save()
                } catch (ColtException e) {
                    ErrorHandler.handle(e, "Can't save the project")
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected String getName() {
                return "Save Project";
            }
        });
    }

    static void saveAsProjectDialog(Scene scene) {
        FileChooser fileChooser = new FileChooser()
        fileChooser.extensionFilters.add(new FileChooser.ExtensionFilter("COLT", "*.colt"))
        Project project = ColtProjectManager.getInstance().currentProject
        fileChooser.initialDirectory = new File(project.path).parentFile
        File file = fileChooser.showSaveDialog(scene.window)
        if (file != null) {
            project.path = file.path
            project.name = file.name[0..-6]
            saveProjectDialog()
        }
    }
}
