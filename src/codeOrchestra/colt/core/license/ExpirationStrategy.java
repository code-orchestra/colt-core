package codeOrchestra.colt.core.license;

/**
 * @author Alexander Eliseyev
 */
public interface ExpirationStrategy {

  boolean isTrialOnly();
  
  boolean allowTrial();
  
  boolean hasExpired();  

  boolean allowsDemo();
  
  boolean isInDemoMode();

  boolean exitIfExpired();

  void handleExpiration();
  
  /**
   * @return whether the user entered a serial number in the dialog
   */
  boolean showLicenseExpiredDialog();

  void showLicenseExpirationInProgressDialog();

  boolean isSubscriptionBased();
  
  boolean showSerialNumberDialog();

}
