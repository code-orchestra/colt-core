package codeOrchestra.colt.core.session;

/**
 * @author Alexander Eliseyev
 */
public interface SocketWriter {
  void writeToSocket(String str);
  void closeSocket();
}