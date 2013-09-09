package codeOrchestra.colt.core.license.strategy

import codeOrchestra.colt.core.errorhandling.ErrorHandler
import codeOrchestra.colt.core.license.CodeOrchestraLicenseManager
import codeOrchestra.colt.core.license.DemoHelper
import codeOrchestra.colt.core.license.ExpirationStrategy
import codeOrchestra.colt.core.license.plimus.PlimusHelper
import codeOrchestra.colt.core.license.plimus.PlimusResponse
import codeOrchestra.colt.core.license.plimus.PlimusResponseStatus
import codeOrchestra.colt.core.logging.Logger
import codeOrchestra.colt.core.ui.ColtApplication
import codeOrchestra.colt.core.ui.dialog.ColtDialogs
import codeOrchestra.colt.core.ui.dialog.SerialNumberDialog
import codeOrchestra.colt.core.ui.dialog.SerialNumberEvent
import codeOrchestra.util.ApplicationUtil
import codeOrchestra.util.DateUtils
import codeOrchestra.util.StringUtils
import javafx.event.EventHandler

import java.util.prefs.BackingStoreException
import java.util.prefs.Preferences

/**
 * @author Alexander Eliseyev
 */
class PlimusSubscriptionWithDemoExpirationStrategy implements ExpirationStrategy {

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

    boolean showSerialNumberDialog() {
        String[] result = new String[1]
        SerialNumberDialog serialNumberDialog = new SerialNumberDialog(ColtApplication.get().getPrimaryStage());
        serialNumberDialog.onInput = { SerialNumberEvent t ->
            result[0] = t.serialNumber
        } as EventHandler
        serialNumberDialog.showInput()

        return processSerialNumber(result[0])
    }

    private boolean processSerialNumber(String serialNumber) {
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
    boolean isTrialOnly() {
        return false;
    }

    @Override
    boolean allowTrial() {
        return false;
    }

    @Override
    boolean exitIfExpired() {
        return true;
    }

    @Override
    void handleExpiration() {
        demoMode = true

        if (!ApplicationUtil.coltStartWasRecentlyRequested()) {
            String expireMessage = String.format("COLT is in Demo mode. Compilations count is limited to %d.", DemoHelper.get().getMaxCompilationsCount() - 1);
            ColtDialogs.showInfo(ColtApplication.get().getPrimaryStage(),
                    "COLT License",
                    expireMessage)
        }
    }

    private boolean haventValidatedOnServerForTooLong() {
        long lastValidationTime = preferences.getLong(LAST_VALIDATION_DATE_STRING, 0);
        if (lastValidationTime == 0) {
            return false;
        }

        return (((System.currentTimeMillis() - lastValidationTime) / DateUtils.MILLIS_PER_DAY) + 1) > 6;
    }

    @Override
    boolean showLicenseExpiredDialog() {
        if (haventValidatedOnServerForTooLong() && !CodeOrchestraLicenseManager.noSerialNumberPresent()) {
            String expireMessage = "Key validation requires an active internet connection. COLT will be launched in Demo mode";

            ColtDialogs.showWarning(ColtApplication.get().getPrimaryStage(),
                    "COLT License",
                    expireMessage);

            return false;
        }

        if (!ApplicationUtil.coltStartWasRecentlyRequested()) {
            Boolean[] result = { false }
            SerialNumberDialog serialNumberDialog = new SerialNumberDialog(ColtApplication.get().getPrimaryStage());
            serialNumberDialog.onInput = { SerialNumberEvent t ->
                if (t.cancelled) {
                    result[0] = false
                } else if (processSerialNumber(t.serialNumber)) {
                    result[0] = true
                }
            } as EventHandler
            serialNumberDialog.show()

            return result[0];
        }

        return false;
    }

    @Override
    void showLicenseExpirationInProgressDialog() {
    }

    @Override
    boolean isSubscriptionBased() {
        return true;
    }

    @Override
    boolean hasExpired() {
        if (CodeOrchestraLicenseManager.noSerialNumberPresent()) {
            return true;
        }

        try {
            PlimusResponse validationResponse = PlimusHelper.validateKey(CodeOrchestraLicenseManager.getSerialNumber());
            return handleValidationResponse(validationResponse);
        } catch (IOException e) {
            return checkIfExpiredLocally();
        }
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
    boolean allowsDemo() {
        return true;
    }

    @Override
    boolean isInDemoMode() {
        return demoMode;
    }

    private static int getSubscriptionDaysLeft() {
        long expirationDateMillis = preferences.getLong(EXPIRE_LOCALLY_MILLIS, System.currentTimeMillis());
        return (int) ((expirationDateMillis - System.currentTimeMillis()) / DateUtils.MILLIS_PER_DAY) + 1;
    }

}
