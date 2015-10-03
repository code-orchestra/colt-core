package codeOrchestra.colt.core;

import codeOrchestra.colt.core.session.LiveCodingSession;
import codeOrchestra.colt.core.session.listener.LiveCodingListener;
import codeOrchestra.colt.core.socket.ClientSocketHandlerAdapter;

import java.util.List;
import java.util.Map;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingManager<S extends ClientSocketHandlerAdapter> extends ColtService {
    void startSession(String broadcastId, String clientId, Map<String, String> clientInfo, S clientSocketHandler);
    void stopSession(LiveCodingSession liveCodingSession);
    void stopAllSession();
    List<LiveCodingSession<S>> getCurrentConnections();
    void addListener(LiveCodingListener listener);
    void removeListener(LiveCodingListener listener);
    void pause();
    void flush();
    boolean isPaused();
    void dispose();
}
