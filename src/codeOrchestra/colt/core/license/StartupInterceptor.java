package codeOrchestra.colt.core.license;

import codeOrchestra.util.StringUtils;

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
// TODO                MessageDialog.openError(Display.getDefault().getActiveShell(), "COLT License", "COLT beta version requires an active internet connection to start.");
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
                };
            }.start();
        }

        // Trial-only (beta versions) - no serial number is checked
        if (expirationStrategy.isTrialOnly()) {
            if (ExpirationHelper.getExpirationStrategy().hasExpired()) {
                expirationStrategy.showLicenseExpiredDialog();
                return StartupInterceptType.EXIT_EXPIRED;
            } else {
                if (UsagePeriods.getInstance().isCurrentTimePresentInUsagePeriods()) {
// TODO                    MessageDialog.openError(Display.getDefault().getActiveShell(), "Evaluation License", "Something is wrong with the system clock\nCOLT was launched already on the currently set time.");
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
// TODO                MessageDialog.openError(Display.getDefault().getActiveShell(), title, "Something is wrong with the system clock\nCOLT was launched already on the currently set time.");
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
                String title = "COLT Subscription";
// TODO:                MessageDialog.openError(Display.getDefault().getActiveShell(), title, "Something is wrong with the system clock\nCOLT was launched already on the currently set time.");
                return StartupInterceptType.EXIT_UNKNOWN;
            }

            boolean expired = false;
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
