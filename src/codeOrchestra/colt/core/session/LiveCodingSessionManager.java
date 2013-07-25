package codeOrchestra.colt.core.session;

import codeOrchestra.colt.core.session.socket.ClientSocketHandler;
import codeOrchestra.colt.core.session.sourcetracking.SourceFile;
import codeOrchestra.colt.core.session.sourcetracking.SourceTrackingListener;

import java.util.List;
import java.util.Map;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingSessionManager<S extends SourceFile> {

    LiveCodingSession getSession(String clientId);

    void sendLiveCodingMessage(String message);

    void startSession(String broadcastId, String clientId, Map<String, String> clientInfo, ClientSocketHandler clientSocketHandler);

    void stopSession(LiveCodingSession liveCodingSession);

    List<LiveCodingSession> getCurrentConnections();

    void addSourceTrackingListener(SourceTrackingListener<S> sourceTrackingListener);

    void removeSourceTrackingListener(SourceTrackingListener<S> sourceTrackingListener);

}
