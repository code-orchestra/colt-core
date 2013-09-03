package codeOrchestra.colt.core.license.strategy;

import codeOrchestra.colt.core.errorhandling.ErrorHandler;
import codeOrchestra.colt.core.license.CodeOrchestraLicenseManager;
import codeOrchestra.colt.core.license.DemoHelper;
import codeOrchestra.colt.core.license.ExpirationStrategy;
import codeOrchestra.colt.core.license.plimus.PlimusHelper;
import codeOrchestra.colt.core.license.plimus.PlimusResponse;
import codeOrchestra.colt.core.license.plimus.PlimusResponseStatus;
import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.colt.core.ui.ColtApplication;
import codeOrchestra.colt.core.ui.dialog.ColtDialogs;
import codeOrchestra.util.DateUtils;
import codeOrchestra.util.StringUtils;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Alexander Eliseyev
 */
public class PlimusSubscriptionWithDemoExpirationStrategy implements ExpirationStrategy {
    private static Logger logger = Logger.getLogger(ErrorHandler.class);

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
        String serialNumber = Dialogs.create()
                .owner(ColtApplication.get().getPrimaryStage())
                .title("Serial number")
                .message("Please type the serial number purchased")
//                .lightweight()
                .nativeTitleBar()
                .showTextInput();

        if (StringUtils.isEmpty(serialNumber)) {
            return false;
        }

        PlimusResponse keyRegistrationResponse;
        try {
            keyRegistrationResponse = PlimusHelper.registerKey(serialNumber);
        } catch (IOException e) {
            ColtDialogs.showError(ColtApplication.get().getPrimaryStage(),
                    "COLT License",
                    "Can't reach the validation server.",
                    "Make sure your internet connection is active.");
            logger.error(e);
            return showSerialNumberDialog();
        }

        if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.ERROR_INVALIDKEY) {
            ColtDialogs.showError(ColtApplication.get().getPrimaryStage(),
                    "Serial number",
                    "The serial number entered is invalid.");
            return showSerialNumberDialog();
        }

        if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.ERROR_INVALIDPRODUCT) {
            ColtDialogs.showError(ColtApplication.get().getPrimaryStage(),
                    "Serial number",
                    "The serial number entered can't be validated.");
            return showSerialNumberDialog();
        }

        if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.ERROR_EXPIREDKEY) {
            ColtDialogs.showError(ColtApplication.get().getPrimaryStage(),
                    "Serial number",
                    "The serial number entered had expired " + Math.abs(keyRegistrationResponse.getDaysTillExpiration()) + " days ago.");
            return showSerialNumberDialog();
        }

        if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.ERROR_MAXCOUNT) {
            ColtDialogs.showError(ColtApplication.get().getPrimaryStage(),
                    "Serial number",
                    "The key entered has already been registered the maximum number of times.");
            return showSerialNumberDialog();
        }

        if (keyRegistrationResponse.getStatus() == PlimusResponseStatus.SUCCESS) {
            ColtDialogs.showApplicationMessage(ColtApplication.get().getPrimaryStage(),
                    "COLT License",
                    "Thank you for choosing the Code Orchestra Livecoding Tool!");
            registerProduct(serialNumber, keyRegistrationResponse);
            return true;
        } else {
            ColtDialogs.showError(ColtApplication.get().getPrimaryStage(),
                    "Serial number",
                    "The serial number entered can't be validated (" + keyRegistrationResponse.getStatus() + ").");
            return showSerialNumberDialog();
        }
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

        ColtDialogs.showInfo(ColtApplication.get().getPrimaryStage(),
                "COLT License",
                expireMessage);
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

            ColtDialogs.showWarning(ColtApplication.get().getPrimaryStage(),
                    "COLT License",
                    expireMessage);

            return false;
        }

        String expireMessage = CodeOrchestraLicenseManager.noSerialNumberPresent() ?
                "No COLT License present" :
                "Your COLT subscription has expired.";

        List<Dialogs.CommandLink> links = Arrays.asList(
                new Dialogs.CommandLink("Continue in Demo mode",
                        String.format("Compilations count would be limited to %d.", DemoHelper.get().getMaxCompilationsCount() - 1)),
                new Dialogs.CommandLink("Enter Serial Number",
                        "Browse to www.codeorchestra.com to purchase one"));

        Action response = Dialogs.create()
                .owner(ColtApplication.get().getPrimaryStage())
                .title("COLT License")
                .message(expireMessage)
//                .lightweight()
                .nativeTitleBar()
                .showCommandLinks(links.get(1), links);

        if (response.textProperty().getBean() == links.get(1)) {
            return showSerialNumberDialog();
        }

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
//            ErrorHandler.handle(e);
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

        preferences.putLong(EXPIRE_LOCALLY_MILLIS, System.currentTimeMillis() + (keyRegistrationResponse.getDaysTillExpiration() * DateUtils.MILLIS_PER_DAY));
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
