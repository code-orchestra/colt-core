package codeOrchestra.colt.core.session;

import codeOrchestra.colt.core.socket.ClientSocketHandlerAdapter;

import java.util.Map;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingSession<S extends ClientSocketHandlerAdapter> {
    String getClientId();
    String getBroadcastId();
    String getBasicClientInfo();
    Map<String, String> getClientInfo();
    long getStartTimestamp();
    S getSocketWrapper();
    void sendLiveCodingMessage(String message, String packageId, boolean addToHistory);
    void sendMessageAsIs(String message);
    int getSessionNumber();
    void dispose();
    boolean isDisposed();
}