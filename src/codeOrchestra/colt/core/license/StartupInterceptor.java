package codeOrchestra.colt.core.license;

import codeOrchestra.colt.core.ui.COLTApplication;
import codeOrchestra.util.StringUtils;
import org.controlsfx.dialog.Dialogs;

/**
 * @author Alexander Eliseyev
 */
public class StartupInterceptor {

    private final static StartupInterceptor instance = new StartupInterceptor();

    public static StartupInterceptor getInstance() {
        return instance;
    }

    public StartupInterceptType interceptStart() {
        final ExpirationStrategy expirationStrategy = ExpirationHelper.getExpirationStrategy();

        // Report serial number every 10 seconds
        if (StringUtils.isNotEmpty(CodeOrchestraLicenseManager.getLegacySerialNumber())) {
            if (expirationStrategy.isTrialOnly() && !new ActivationReporter(CodeOrchestraLicenseManager.getLegacySerialNumber()).report()) {
                Dialogs.create()
                        .owner(COLTApplication.get().getPrimaryStage())
                        .title("COLT License")
                        .message("COLT beta version requires an active internet connection to start.")
                        .nativeChrome()
                        .showError();
                return StartupInterceptType.EXIT_NO_CONNECTION;
            }

            new Thread() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                        new ActivationReporter(CodeOrchestraLicenseManager.getLegacySerialNumber()).report();
                    }
                }

                ;
            }.start();
        }

        // Trial-only (beta versions) - no serial number is checked
        if (expirationStrategy.isTrialOnly()) {
            if (ExpirationHelper.getExpirationStrategy().hasExpired()) {
                expirationStrategy.showLicenseExpiredDialog();
                return StartupInterceptType.EXIT_EXPIRED;
            } else {
                if (UsagePeriods.getInstance().isCurrentTimePresentInUsagePeriods()) {
                    Dialogs.create()
                            .owner(COLTApplication.get().getPrimaryStage())
                            .title("Evaluation License")
                            .message("Something is wrong with the system clock\nCOLT was launched already on the currently set time.")
                            .nativeChrome()
                            .showError();

                    return StartupInterceptType.EXIT_UNKNOWN;
                }

                expirationStrategy.showLicenseExpirationInProgressDialog();

                return StartupInterceptType.START;
            }
        }

        // No-trial version (serial-number only)
        if (!expirationStrategy.allowTrial() && CodeOrchestraLicenseManager.noSerialNumberPresent() && !expirationStrategy.allowsDemo()) {
            if (!expirationStrategy.showLicenseExpiredDialog()) {
                return StartupInterceptType.EXIT_EXPIRED;
            }
        }

        // Trial version with no serial
        if ((expirationStrategy.allowTrial() && CodeOrchestraLicenseManager.noSerialNumberPresent()) || (!expirationStrategy.allowsDemo() && expirationStrategy.isSubscriptionBased() && !CodeOrchestraLicenseManager.noSerialNumberPresent())) {
            if (UsagePeriods.getInstance().isCurrentTimePresentInUsagePeriods()) {
                String title = expirationStrategy.isSubscriptionBased() ? "COLT Subscription" : "Evaluation License";
                Dialogs.create()
                        .owner(COLTApplication.get().getPrimaryStage())
                        .title(title)
                        .message("Something is wrong with the system clock\nCOLT was launched already on the currently set time.")
                        .nativeChrome()
                        .showError();

                return StartupInterceptType.EXIT_UNKNOWN;
            }

            boolean expired;
            if (ExpirationHelper.getExpirationStrategy().hasExpired()) {
                expired = !expirationStrategy.showLicenseExpiredDialog();
            } else {
                expirationStrategy.showLicenseExpirationInProgressDialog();
                expired = false;
            }

            if (expired) {
                expirationStrategy.handleExpiration();

                if (expirationStrategy.exitIfExpired()) {
                    return StartupInterceptType.EXIT_EXPIRED;
                }
            }
        }

        // Demo version with subscription
        if (expirationStrategy.allowsDemo() && expirationStrategy.isSubscriptionBased()) {
            if (UsagePeriods.getInstance().isCurrentTimePresentInUsagePeriods()) {
                Dialogs.create()
                        .owner(COLTApplication.get().getPrimaryStage())
                        .title("COLT Subscription")
                        .message("Something is wrong with the system clock\nCOLT was launched already on the currently set time.")
                        .nativeChrome()
                        .showError();

                return StartupInterceptType.EXIT_UNKNOWN;
            }

            boolean expired;
            if (ExpirationHelper.getExpirationStrategy().hasExpired()) {
                expired = !expirationStrategy.showLicenseExpiredDialog();
            } else {
                expirationStrategy.showLicenseExpirationInProgressDialog();
                expired = false;
            }

            if (expired) {
                expirationStrategy.handleExpiration();
            }
        }

        return StartupInterceptType.START;
    }

}
