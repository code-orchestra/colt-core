package codeOrchestra.colt.core.socket;

import java.net.Socket;

/**
 * @author Alexander Eliseyev
 */
public interface ClientSocketHandlerFactory {
  ClientSocketHandler createHandler(Socket socket);
}