package codeOrchestra.colt.core;

import codeOrchestra.colt.core.model.COLTProject;
import codeOrchestra.colt.core.session.LiveCodingSession;
import codeOrchestra.colt.core.session.listener.LiveCodingListener;

import java.util.*;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractLiveCodingManager<P extends COLTProject> implements LiveCodingManager<P> {

    private Object listenerMonitor = new Object();

    protected Map<String, LiveCodingSession> currentSessions = new HashMap<String, LiveCodingSession>();

    private List<LiveCodingListener> liveCodingListeners = new ArrayList<LiveCodingListener>();

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

    protected void fireSessionStart(LiveCodingSession session) {
        synchronized (listenerMonitor) {
            for (LiveCodingListener listener : liveCodingListeners) {
                listener.onSessionStart(session);
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
