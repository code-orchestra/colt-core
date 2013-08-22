package codeOrchestra.colt.core;

import codeOrchestra.colt.core.model.COLTProject;
import codeOrchestra.colt.core.session.LiveCodingSession;
import codeOrchestra.colt.core.session.listener.LiveCodingListener;
import codeOrchestra.colt.core.socket.ClientSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingManager<P extends COLTProject, S> extends COLTService<P> {

    LiveCodingSession getSession(String clientId);

    void startSession(String broadcastId, String clientId, Map<String, String> clientInfo, S clientSocketHandler);

    void stopSession(LiveCodingSession liveCodingSession);

    Set<String> getCurrentSessionsClientIds();

    List<LiveCodingSession> getCurrentConnections();

    void addListener(LiveCodingListener listener);

    void removeListener(LiveCodingListener listener);

    void dispose();

}
