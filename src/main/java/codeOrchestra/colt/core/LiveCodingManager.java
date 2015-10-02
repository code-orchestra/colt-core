package codeOrchestra.colt.core;

import codeOrchestra.colt.core.session.LiveCodingSession;
import codeOrchestra.colt.core.session.listener.LiveCodingListener;
import codeOrchestra.colt.core.socket.ClientSocketHandlerAdapter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingManager<S extends ClientSocketHandlerAdapter> extends ColtService {
    LiveCodingSession getSession(String clientId);
    void startSession(String broadcastId, String clientId, Map<String, String> clientInfo, S clientSocketHandler);
    void stopSession(LiveCodingSession liveCodingSession);
    void stopAllSession();
    Set<String> getCurrentSessionsClientIds();
    List<LiveCodingSession<S>> getCurrentConnections();
    void addListener(LiveCodingListener listener);
    void removeListener(LiveCodingListener listener);
    void pause();
    void flush();
    boolean isPaused();
    void dispose();
}
