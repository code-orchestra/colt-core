package codeOrchestra.colt.core.ui.dialog

import codeOrchestra.colt.core.ColtException
import codeOrchestra.colt.core.ColtProjectManager
import codeOrchestra.colt.core.errorhandling.ErrorHandler
import codeOrchestra.colt.core.tasks.ColtTaskWithProgress
import codeOrchestra.colt.core.tasks.TasksManager
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
        // open demo projects directories
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
}
