package codeOrchestra.colt.core;

import codeOrchestra.colt.core.model.Project;
import codeOrchestra.colt.core.session.LiveCodingSession;
import codeOrchestra.colt.core.session.listener.LiveCodingListener;

import java.util.*;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractLiveCodingManager<P extends Project, S> implements LiveCodingManager<P, S> {

    private Object listenerMonitor = new Object();

    protected Map<String, LiveCodingSession<S>> currentSessions = new HashMap<>();

    private List<LiveCodingListener> liveCodingListeners = new ArrayList<>();

    protected boolean paused;

    @Override
    public List<LiveCodingSession<S>> getCurrentConnections() {
        List<LiveCodingSession<S>> liveCodingSessions = new ArrayList<>(currentSessions.values());
        Collections.sort(liveCodingSessions, new Comparator<LiveCodingSession>() {
            @Override
            public int compare(LiveCodingSession s1, LiveCodingSession s2) {
                return s1.getSessionNumber() - s2.getSessionNumber();
            }
        });
        return liveCodingSessions;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public final void pause() {
        paused = true;

        fireSessionsPaused();
    }

    @Override
    public final void flush() {
        doFlush();

        paused = false;
        fireSessionsResumed();
    }

    protected abstract void doFlush();

    @Override
    public LiveCodingSession getSession(String clientId) {
        return currentSessions.get(clientId);
    }

    @Override
    public Set<String> getCurrentSessionsClientIds() {
        return currentSessions.keySet();
    }

    @Override
    public void addListener(LiveCodingListener listener) {
        synchronized (listenerMonitor) {
            liveCodingListeners.add(listener);
        }
    }

    @Override
    public void removeListener(LiveCodingListener listener) {
        synchronized (listenerMonitor) {
            liveCodingListeners.remove(listener);
        }
    }

    protected void fireCodeUpdate() {
        synchronized (listenerMonitor) {
            for (LiveCodingListener listener : liveCodingListeners) {
                listener.onCodeUpdate();
            }
        }
    }

    protected void fireSessionStart(LiveCodingSession session) {
        synchronized (listenerMonitor) {
            for (LiveCodingListener listener : liveCodingListeners) {
                listener.onSessionStart(session);
            }
        }
    }

    private void fireSessionsPaused() {
        synchronized (listenerMonitor) {
            for (LiveCodingListener listener : liveCodingListeners) {
                listener.onSessionPause();
            }
        }
    }

    private void fireSessionsResumed() {
        synchronized (listenerMonitor) {
            for (LiveCodingListener listener : liveCodingListeners) {
                listener.onSessionResume();
            }
        }
    }

    protected void fireSessionEnd(LiveCodingSession session) {
        synchronized (listenerMonitor) {
            for (LiveCodingListener listener : liveCodingListeners) {
                listener.onSessionEnd(session);
            }
        }
    }

    @Override
    public void dispose() {
        synchronized (listenerMonitor) {
            liveCodingListeners.clear();
        }
    }
}
