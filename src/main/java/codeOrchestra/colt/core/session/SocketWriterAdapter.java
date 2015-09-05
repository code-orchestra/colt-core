package codeOrchestra.colt.core.session;

import codeOrchestra.colt.core.socket.ClientSocketHandlerAdapter;

/**
 * @author Alexander Eliseyev
 */
public class SocketWriterAdapter implements ClientSocketHandlerAdapter {

    private SocketWriter socketWriter;

    public SocketWriterAdapter(SocketWriter socketWriter) {
        this.socketWriter = socketWriter;
    }

    @Override
    public void sendMessage(String message) {
        socketWriter.writeToSocket(message);
    }

    public void close() {
        socketWriter.closeSocket();
    }

}
