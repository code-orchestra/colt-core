package codeOrchestra.colt.core.ui.dialog

import codeOrchestra.colt.core.ColtException
import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.RecentProjects
import codeOrchestra.colt.core.errorhandling.ErrorHandler
import codeOrchestra.colt.core.model.Project
import codeOrchestra.colt.core.tasks.ColtTaskWithProgress
import codeOrchestra.colt.core.tasks.TasksManager
import codeOrchestra.colt.core.ui.ColtApplication
import codeOrchestra.colt.core.ui.components.IProgressIndicator
import javafx.scene.Scene
import javafx.stage.FileChooser

/**
 * @author Dima Kruk
 */
class ProjectDialogs {

    static void newAsProjectDialog(Scene scene){
        FileChooser fileChooser = new FileChooser()
        fileChooser.extensionFilters.add(new FileChooser.ExtensionFilter("COLT", "*.colt"))
        File file = fileChooser.showSaveDialog(scene.window)
        if (file != null) {
            try {
                // TODO: a handler must be defined by the user (AS, JS, etc)
                ColtProjectManager.instance.create("AS", file.name[0..-6], file)
            } catch (ColtException e) {
                ErrorHandler.handle(e, "Can't create a new project")
            }
        }
    }

    static void newJsProjectDialog(Scene scene){
        FileChooser fileChooser = new FileChooser()
        fileChooser.extensionFilters.add(new FileChooser.ExtensionFilter("COLT", "*.colt"))
        File file = fileChooser.showSaveDialog(scene.window)
        if (file != null) {
            try {
                // TODO: a handler must be defined by the user (AS, JS, etc)
                ColtProjectManager.instance.create("JS", file.name[0..-6], file)
            } catch (ColtException e) {
                ErrorHandler.handle(e, "Can't create a new project")
            }
        }
    }

    static void openProjectDialog(Scene scene){
        FileChooser fileChooser = new FileChooser()
        fileChooser.extensionFilters.add(new FileChooser.ExtensionFilter("COLT", "*.colt"))
        File file = fileChooser.showOpenDialog(scene.window)
        if (file != null) {
            try {
                ColtProjectManager.instance.load(file.getPath())
            } catch (ColtException e) {
                ErrorHandler.handle(e, "Can't load the project")
            }
        }
    }

    static void openDemoProjectDialog(Scene scene){
        println "need implement: ProjectDialogs.openDemoProjectDialog()"
        //todo: open demo projects directories
    }

    static void openWelcomeScreen(Scene scene){
        closeProjectDialog()//todo: или оставить текущий проект?
    }

    static void closeProjectDialog(){
        ColtApplication.get().showWelcomeScreen()
        ColtApplication.get().closeProject()
    }

    static void saveProjectDialog(){
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

    static void saveAsProjectDialog(Scene scene){
        FileChooser fileChooser = new FileChooser()
        fileChooser.extensionFilters.add(new FileChooser.ExtensionFilter("COLT", "*.colt"))
        Project project = ColtProjectManager.getInstance().currentProject
        fileChooser.initialDirectory = new File(project.path).parentFile
        File file = fileChooser.showSaveDialog(scene.window)
        if (file != null){
            project.path = file.path
            project.name = file.name[0..-6]
            saveProjectDialog()
        }
    }
}
