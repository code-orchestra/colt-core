package codeOrchestra.colt.core.license.strategy;

import codeOrchestra.colt.core.errorhandling.ErrorHandler;
import codeOrchestra.colt.core.license.CodeOrchestraLicenseManager;
import codeOrchestra.colt.core.license.DemoHelper;
import codeOrchestra.colt.core.license.ExpirationStrategy;
import codeOrchestra.colt.core.license.plimus.PlimusHelper;
import codeOrchestra.colt.core.license.plimus.PlimusResponse;
import codeOrchestra.colt.core.license.plimus.PlimusResponseStatus;
import codeOrchestra.util.DateUtils;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Alexander Eliseyev
 */
public class PlimusSubscriptionWithDemoExpirationStrategy implements ExpirationStrategy {

  private static final String EXPIRE_LOCALLY_MILLIS = "expireLocally";
  private static final String LAST_VALIDATION_DATE_STRING = "lastValidationDate";

  private static Preferences preferences = Preferences.userNodeForPackage(CodeOrchestraLicenseManager.class);

  private boolean demoMode;
  
  protected boolean handleValidationResponse(PlimusResponse plimusResponse) {
    preferences.putLong(LAST_VALIDATION_DATE_STRING, System.currentTimeMillis());
    try {
      preferences.sync();
    } catch (BackingStoreException e) {
      throw new RuntimeException("Can't store key validation time");
    }
    
    if (plimusResponse.getStatus() == PlimusResponseStatus.SUCCESS) {
      return false;
    }

    return true;
  }

  public boolean showSerialNumberDialog() {
    // TODO: implement

      /*
      InputDialog inputDialog = new InputDialog(Display.getDefault().getActiveShell(), "Serial number", "Please type the serial number purchased", null, null);
    if (inputDialog.open() == Window.CANCEL) {
      return false;
    }
    
    String serialNumber = inputDialog.getValue();
    if (serialNumber != null) {
      PlimusResponse keyRegistrationResponse;
      try {        
        keyRegistrationResponse = PlimusHelper.registerKey(serialNumber);
      } catch (IOException e) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "Can't reach the validation server. Make sure your internet connection is active.");
        ErrorHandler.handle(e);
        return showSerialNumberDialog();
      }      
      
      if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.ERROR_INVALIDKEY) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "The serial number entered is invalid.");
        return showSerialNumberDialog();
      }

      if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.ERROR_INVALIDPRODUCT) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "The serial number entered can't be validated.");
        return showSerialNumberDialog();
      }
      
      if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.ERROR_EXPIREDKEY) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "The serial number entered had expired " + Math.abs(keyRegistrationResponse.getDaysTillExpiration()) + " days ago.");
        return showSerialNumberDialog();
      }

      if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.ERROR_MAXCOUNT) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "The key entered has already been registered the maximum number of times.");
        return showSerialNumberDialog();
      }
      
      if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.SUCCESS) {
        MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Serial number", "Thank you for choosing the Code Orchestra Livecoding Tool!");        
        registerProduct(serialNumber, keyRegistrationResponse);
        return true;
      } else {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Serial number", "The serial number entered can't be validated (" + keyRegistrationResponse.getStatus() + ").");
        return showSerialNumberDialog();
      }
    }
    */

    return false;
  }
  
  private boolean checkIfExpiredLocally() {
    if (haventValidatedOnServerForTooLong()) {
      return true;
    }
    return getSubscriptionDaysLeft() < 1;
  }

  @Override
  public boolean isTrialOnly() {
    return false;
  }

  @Override
  public boolean allowTrial() {
    return false;
  }

  @Override
  public boolean exitIfExpired() {
    return true;
  }

  @Override
  public void handleExpiration() {
    demoMode = true;
    
    String expireMessage = String.format("COLT is in Demo mode. Compilations count is limited to %d.", DemoHelper.get().getMaxCompilationsCount() - 1);

      // TODO: implement

//    MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "COLT License", null,
//        expireMessage, MessageDialog.INFORMATION, new String[] { "OK" }, 0);
//    dialog.open();
  }
  
  private boolean haventValidatedOnServerForTooLong() {
    long lastValidationTime = preferences.getLong(LAST_VALIDATION_DATE_STRING, 0);
    if (lastValidationTime == 0) {
      return false;
    }
    
    return (((System.currentTimeMillis() - lastValidationTime) / DateUtils.MILLIS_PER_DAY) + 1) > 6;
  }

  @Override
  public boolean showLicenseExpiredDialog() {
    if (haventValidatedOnServerForTooLong() && !CodeOrchestraLicenseManager.noSerialNumberPresent()) {
      String expireMessage = "Key validation requires an active internet connection. COLT will be launched in Demo mode";

        // TODO: implement

//      MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "COLT License", null,
//          expireMessage, MessageDialog.INFORMATION, new String[] { "OK" }, 0);
//      dialog.open();
      
      return false;      
    }
    
    String expireMessage = CodeOrchestraLicenseManager.noSerialNumberPresent() ?
        "Browse to www.codeorchestra.com to purchase a subscription or continue in Demo mode." :
        "Your COLT subscription has expired. Browse to www.codeorchestra.com to update the subscription or continue in Demo mode.";

      // TODO: implement

//    MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "COLT License", null,
//        expireMessage, MessageDialog.INFORMATION, new String[] { "Continue in Demo mode", "Enter Serial Number" }, 1);
//    int result = dialog.open();
//    if (result == 1) {
//      return showSerialNumberDialog();
//    }
    
    return false;
  }

  @Override
  public void showLicenseExpirationInProgressDialog() {
  }

  @Override
  public boolean isSubscriptionBased() {
    return true;
  }

  @Override
  public boolean hasExpired() {
    if (CodeOrchestraLicenseManager.noSerialNumberPresent()) {
      return true;
    }
    
    try {
      PlimusResponse validationResponse = PlimusHelper.validateKey(CodeOrchestraLicenseManager.getSerialNumber());
      return handleValidationResponse(validationResponse);
    } catch (IOException e) {
      ErrorHandler.handle(e);
      return checkIfExpiredLocally();
    }
  }

  private int getSubscriptionDaysLeft() {
    long expirationDateMillis = preferences.getLong(EXPIRE_LOCALLY_MILLIS, System.currentTimeMillis());
    return (int) ((expirationDateMillis - System.currentTimeMillis()) / DateUtils.MILLIS_PER_DAY) + 1;
  }
  
  protected void registerProduct(String serialNumber, PlimusResponse keyRegistrationResponse) {
    demoMode = false;
    
    CodeOrchestraLicenseManager.registerProduct(serialNumber);

    preferences.putLong(
        EXPIRE_LOCALLY_MILLIS,
        System.currentTimeMillis() + (keyRegistrationResponse.getDaysTillExpiration() * DateUtils.MILLIS_PER_DAY));
    try {
      preferences.sync();
    } catch (BackingStoreException e) {
      throw new RuntimeException("Can't sync license data", e);
    }
  }
  
  @Override
  public boolean allowsDemo() {
    return true;
  }

  @Override
  public boolean isInDemoMode() {
    return demoMode;
  }
  
}
