package codeOrchestra.colt.core.rpc;

import codeOrchestra.colt.core.rpc.security.InvalidAuthTokenException;
import codeOrchestra.colt.core.rpc.security.InvalidShortCodeException;
import codeOrchestra.colt.core.rpc.security.TooManyFailedCodeTypeAttemptsException;

/**
 * @author Alexander Eliseyev
 */
public interface COLTRemoteService {
  
  // Authorization methods
  
  void requestShortCode(String requestor) throws COLTRemoteTransferableException;
  
  String obtainAuthToken(String shortCode) throws TooManyFailedCodeTypeAttemptsException, InvalidShortCodeException;
  
  void checkAuth(String securityToken) throws InvalidAuthTokenException;

  int ping();

}
