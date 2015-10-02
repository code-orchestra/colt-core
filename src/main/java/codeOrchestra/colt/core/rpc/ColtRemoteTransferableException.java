package codeOrchestra.colt.core.rpc;

/**
 * @author Alexander Eliseyev
 */
public class ColtRemoteTransferableException extends ColtRemoteException {
  public ColtRemoteTransferableException() {
    super();
  }
  public ColtRemoteTransferableException(String message) {
    super(message);
  }
}