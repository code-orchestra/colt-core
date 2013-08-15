package codeOrchestra.colt.core.tasks;

import codeOrchestra.colt.core.logging.Logger;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressIndicator;
import org.controlsfx.dialog.Dialogs;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Alexander Eliseyev
 */
public class TasksManager {

    private static final int MAX_EXECUTION_TIME = 100;

    private Logger LOG = Logger.getLogger(TasksManager.class);

    private static TasksManager instance;

    public static synchronized TasksManager getInstance() {
        if (instance == null) {
            instance = new TasksManager();
        }
        return instance;
    }

    private final Thread executorThread;

    /*  Notified when:
     *    tasksQueue queue becomes non-empty
     *    workerStarted becomes false
     */
    private final Object myLock = new Object();

    private ConcurrentLinkedQueue<COLTTask> tasksQueue = new ConcurrentLinkedQueue<COLTTask>();

    public TasksManager() {
        executorThread = new Executor();
        executorThread.setDaemon(true);
        executorThread.start();
    }

    private class Executor extends Thread {

        private volatile boolean workerStarted = false;

        private Executor() {
            super("Executor");
        }

        private class COLTTaskEventHandler implements EventHandler<WorkerStateEvent> {

            private boolean disposed;

            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                if (workerStateEvent.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED ||
                    workerStateEvent.getEventType() == WorkerStateEvent.WORKER_STATE_CANCELLED ||
                    workerStateEvent.getEventType() == WorkerStateEvent.WORKER_STATE_FAILED) {

                    COLTTask doneTask;
                    synchronized (myLock) {
                        if (disposed) {
                            return;
                        }

                        disposed = true;

                        doneTask = tasksQueue.remove();
                        workerStarted = false;
                        myLock.notifyAll();
                    }

                    if (workerStateEvent.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
                        try {
                            doneTask.onOK(doneTask.get());
                        } catch (Throwable t) {
                            LOG.error(t);
                        }
                    } else {
                        doneTask.onFail();
                    }

                }
            }
        };

        public void run() {
            try {
                while (true) {
                    synchronized (myLock) {
                        if (workerStarted || tasksQueue.isEmpty()) {
                            try {
                                myLock.wait();
                            } catch (InterruptedException e) {
                                /* ignore */
                            }
                        }
                        if (workerStarted) {
                            continue;
                        }

                        COLTTask first = tasksQueue.peek();
                        if (first != null) {
                            workerStarted = true;

                            EventHandler<WorkerStateEvent> eventHandler = new COLTTaskEventHandler();

                            first.setOnSucceeded(eventHandler);
                            first.setOnCancelled(eventHandler);
                            first.setOnFailed(eventHandler);

                            new Thread(first).start();
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }

    private void schedule(final COLTTask task) {
        synchronized (myLock) {
            if (tasksQueue.isEmpty()) {
                myLock.notifyAll();
            }
            tasksQueue.offer(task);
        }
    }

    private ProgressIndicator getProgressIndicator() {
        // TODO: implement
        return null;
    }

    public void scheduleModalTask(COLTTask task) {
        Dialogs.create()
                .title(task.getName())
//                .lightweight()
                .nativeTitleBar()
                .showWorkerProgress(task);
        schedule(task);
    }

    public <R> R scheduleBackgroundTask(COLTTask<R> task) {
        // TODO: implement
        return null;
    }

}
