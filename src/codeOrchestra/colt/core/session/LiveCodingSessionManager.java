package codeOrchestra.colt.core.session;

import codeOrchestra.colt.core.session.socket.ClientSocketHandler;

import java.util.List;
import java.util.Map;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingSessionManager {

    LiveCodingSession getSession(String clientId);

    void sendLiveCodingMessage(String message);

    void startSession(String broadcastId, String clientId, Map<String, String> clientInfo, ClientSocketHandler clientSocketHandler);

    void stopSession(LiveCodingSession liveCodingSession);

    List<LiveCodingSession> getCurrentConnections();

}
