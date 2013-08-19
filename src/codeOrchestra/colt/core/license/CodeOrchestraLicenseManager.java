package codeOrchestra.colt.core.license;


import codeOrchestra.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Alexander Eliseyev
 */
public class CodeOrchestraLicenseManager {

    private static Preferences preferences = Preferences.userNodeForPackage(CodeOrchestraLicenseManager.class);

    private static final String SERIAL_NUMBER_KEY = "sn2";
    private static final String LEGACY_NUMBER_KEY = "serial-number";
    private static final String LICENSED_TO_KEY = "licensed-to";

    private static List<LicenseListener> licenseListeners = new ArrayList<>();

    public static synchronized void addListener(LicenseListener licenseListener) {
        licenseListeners.add(licenseListener);
    }

    public static synchronized void removeListener(LicenseListener licenseListener) {
        licenseListeners.remove(licenseListener);
    }

    private static synchronized void fireRegistered() {
        for (LicenseListener licenseListener : licenseListeners) {
            licenseListener.onRegister();
        }
    }

    public static void clearLicenseDate() {
        preferences.put(SERIAL_NUMBER_KEY, StringUtils.EMPTY);
        preferences.put(LICENSED_TO_KEY, StringUtils.EMPTY);

        try {
            preferences.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException("Can't sync license data", e);
        }
    }

    public static void main(String[] args) {
        clearLicenseDate();
    }

    public static String getLegacySerialNumber() {
        return preferences.get(LEGACY_NUMBER_KEY, StringUtils.EMPTY);
    }

    public static String getSerialNumber() {
        return preferences.get(SERIAL_NUMBER_KEY, StringUtils.EMPTY);
    }

    public static boolean noSerialNumberPresent() {
        return StringUtils.isEmpty(getSerialNumber());
    }

    public static void registerProduct(String serialNumber) {
        preferences.put(SERIAL_NUMBER_KEY, serialNumber);

        try {
            preferences.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException("Can't sync license expiry data", e);
        }

        fireRegistered();
    }

}
