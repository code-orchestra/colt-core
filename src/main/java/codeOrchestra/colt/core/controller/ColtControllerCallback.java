package codeOrchestra.colt.core.controller;

/**
 * @author Alexander Eliseyev
 */
public interface ColtControllerCallback<S, E> {
  
  void onComplete(S successResult);
  
  void onError(Throwable t, E errorResult);

}
