package codeOrchestra.colt.core.tasks;

import javafx.scene.control.ProgressIndicator;
import org.controlsfx.dialog.Dialogs;

/**
 * @author Alexander Eliseyev
 */
public class TasksManager {

    private static TasksManager instance;

    public static synchronized TasksManager getInstance() {
        if (instance == null) {
            instance = new TasksManager();
        }
        return instance;
    }

    private ProgressIndicator getProgressIndicator() {
        // TODO: implement
        return null;
    }

    public void scheduleSyncModalTask(COLTTask task) {
        // TODO: Implement queue

        Dialogs.create().title(task.getName()).lightweight().showWorkerProgress(task);
        Thread taskThread = new Thread(task);
        taskThread.start();
    }

    public <R> R scheduleBackgroundTask(COLTTask<R> task) {
        // TODO: implement
        return null;
    }

}
