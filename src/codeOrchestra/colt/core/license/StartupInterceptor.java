package codeOrchestra.colt.core.license;

import codeOrchestra.colt.core.ui.ColtApplication;
import codeOrchestra.colt.core.ui.dialog.ColtDialogs;

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

        // Trial-only (beta versions) - no serial number is checked
        if (expirationStrategy.isTrialOnly()) {
            if (ExpirationHelper.getExpirationStrategy().hasExpired()) {
                expirationStrategy.showLicenseExpiredDialog();
                return StartupInterceptType.EXIT_EXPIRED;
            } else {
                if (UsagePeriods.getInstance().isCurrentTimePresentInUsagePeriods()) {
                    ColtDialogs.showError(ColtApplication.get().getPrimaryStage(),
                            "Evaluation License",
                            "Something is wrong with the system clock",
                            "COLT was launched already on the currently set time.");

                    return StartupInterceptType.EXIT_UNKNOWN;
                }

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
                ColtDialogs.showError(ColtApplication.get().getPrimaryStage(),
                        title,
                        "Something is wrong with the system clock",
                        "COLT was launched already on the currently set time.");

                return StartupInterceptType.EXIT_UNKNOWN;
            }

            boolean expired;
            if (ExpirationHelper.getExpirationStrategy().hasExpired()) {
                expired = !expirationStrategy.showLicenseExpiredDialog();
            } else {
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
                ColtDialogs.showError(ColtApplication.get().getPrimaryStage(),
                        "COLT Subscription",
                        "Something is wrong with the system clock",
                        "COLT was launched already on the currently set time.");

                return StartupInterceptType.EXIT_UNKNOWN;
            }

            boolean expired;
            if (ExpirationHelper.getExpirationStrategy().hasExpired()) {
                expired = !expirationStrategy.showLicenseExpiredDialog();
            } else {
                expired = false;
            }

            if (expired) {
                expirationStrategy.handleExpiration();
            }
        }

        return StartupInterceptType.START;
    }

}
