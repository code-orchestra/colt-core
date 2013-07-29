package codeOrchestra.colt.core.rpc;

/**
 * @author Alexander Eliseyev
 */
public class COLTRemoteException extends Exception {

  public COLTRemoteException() {
    super();
  }

  public COLTRemoteException(String message, Throwable cause) {
    super(message, cause);
  }

  public COLTRemoteException(String message) {
    super(message);
  }

  public COLTRemoteException(Throwable cause) {
    super(cause);
  }

}
