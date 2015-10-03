package codeOrchestra.colt.core;

import codeOrchestra.colt.core.session.LiveCodingSession;
import codeOrchestra.colt.core.session.listener.LiveCodingListener;
import codeOrchestra.colt.core.socket.ClientSocketHandlerAdapter;

import java.util.*;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractLiveCodingManager<S extends ClientSocketHandlerAdapter> implements LiveCodingManager<S> {

    private final Object listenerMonitor = new Object();

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
    public void stopAllSession() {
        getCurrentConnections().forEach(this::stopSession);
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
            liveCodingListeners.forEach(LiveCodingListener::onCodeUpdate);
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
            liveCodingListeners.forEach(LiveCodingListener::onSessionPause);
        }
    }

    private void fireSessionsResumed() {
        synchronized (listenerMonitor) {
            liveCodingListeners.forEach(LiveCodingListener::onSessionResume);
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