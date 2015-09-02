package codeOrchestra.colt.core.rpc.security;

/**
 * @author Alexander Eliseyev
 */
public interface ColtRemoteSecurityListener {

  void onNewRequest(String requestor, String shortCode);
  
  void onSuccessfulActivation(String shortCode);
  
}
