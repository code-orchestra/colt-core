package codeOrchestra.colt.core.rpc;

import codeOrchestra.util.ExceptionUtils;

/**
 * @author Alexander Eliseyev
 */
public class ColtUnhandledException extends ColtRemoteTransferableException {

  public ColtUnhandledException(Throwable cause) {
    super(getCauseMessage(cause));
  }

  private static String getCauseMessage(Throwable cause) {
    return cause.getClass().getSimpleName() + (cause.getMessage() != null ? ": " + cause.getMessage() : "") + ": " + ExceptionUtils.getStackTrace(cause);
  }
}