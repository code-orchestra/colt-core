package codeOrchestra.colt.core.tasks;

import codeOrchestra.colt.core.logging.Logger;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Alexander Eliseyev
 */
public class TasksManager {

    private Logger LOG = Logger.getLogger(TasksManager.class);

    private static TasksManager instance;

    public static synchronized TasksManager getInstance() {
        if (instance == null) {
            instance = new TasksManager();
        }
        return instance;
    }

    private final Executor executorThread;

    /**
     *  Notified when:
     *  tasksQueue queue becomes non-empty
     *  workerStarted becomes false
     */
    private final Object myLock = new Object();

    private ConcurrentLinkedQueue<ColtTask> tasksQueue = new ConcurrentLinkedQueue<>();

    public TasksManager() {
        executorThread = new Executor();
        executorThread.setDaemon(true);
        executorThread.start();
    }

    public void dispose() {
        executorThread.stopRightThere();
        synchronized (myLock) {
            myLock.notifyAll();
        }
    }

    private class Executor extends Thread {

        private volatile boolean workerStarted = false;

        private boolean mustStop;

        private Executor() {
            super("Executor");
        }

        public void stopRightThere() {
            mustStop = true;
        }

        private class ColtTaskEventHandler implements EventHandler<WorkerStateEvent> {

            private boolean disposed;

            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                if (workerStateEvent.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED ||
                    workerStateEvent.getEventType() == WorkerStateEvent.WORKER_STATE_CANCELLED ||
                    workerStateEvent.getEventType() == WorkerStateEvent.WORKER_STATE_FAILED) {

                    ColtTask doneTask;
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
        }

        public void run() {
            try {
                while (!mustStop) {
                    synchronized (myLock) {
                        if (workerStarted || tasksQueue.isEmpty()) {
                            try {
                                myLock.wait();
                            } catch (InterruptedException e) {
                                /* ignore */
                            }
                        }
                        if (mustStop) {
                            return;
                        }
                        if (workerStarted) {
                            continue;
                        }

                        ColtTask first = tasksQueue.peek();
                        if (first != null) {
                            workerStarted = true;

                            EventHandler<WorkerStateEvent> eventHandler = new ColtTaskEventHandler();

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

    private void schedule(final ColtTask task) {
        synchronized (myLock) {
            if (tasksQueue.isEmpty()) {
                myLock.notifyAll();
            }
            tasksQueue.offer(task);
        }
    }

    public void scheduleBackgroundTask(ColtTask task) {
        schedule(task);
    }

}
