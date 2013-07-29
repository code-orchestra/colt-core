package codeOrchestra.colt.core.rpc;

/**
 * @author Alexander Eliseyev
 */
public class COLTRemoteTransferableException extends COLTRemoteException {

  public COLTRemoteTransferableException() {
    super();
  }

  public COLTRemoteTransferableException(String message, Throwable cause) {
    super(message, cause);
  }

  public COLTRemoteTransferableException(String message) {
    super(message);
  }

  public COLTRemoteTransferableException(Throwable cause) {
    super(cause);
  }

}
