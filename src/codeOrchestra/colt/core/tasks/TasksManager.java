package codeOrchestra.colt.core.tasks;

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

    public <R> R scheduleModalTask(COLTTask<R> task) {
        // TODO: implement
        return null;
    }

    public <R> R scheduleBackgroundTask(COLTTask<R> task) {
        // TODO: implement
        return null;
    }

}
