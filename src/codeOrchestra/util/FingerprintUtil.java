package codeOrchestra.util;

import java.math.BigInteger;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author Alexander Eliseyev
 */
public final class FingerprintUtil {

    public static String getFingerPrint() {
        StringBuilder resultSB = new StringBuilder();
        try {
            for (final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                final NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                if (networkInterface.isLoopback()) {
                    continue;
                }

                byte[] mac = networkInterface.getHardwareAddress();
                if (mac == null) {
                    continue;
                }

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }

                if ("00-00-00-00-00-00-00-E0".equals(sb.toString())) {
                    continue;
                }

                resultSB.append(sb);
                if (interfaces.hasMoreElements()) {
                    resultSB.append("|");
                }
            }
        } catch (Exception e) {
            // ignore
        }

        String result = resultSB.toString();
        if (result.endsWith("|")) {
            return result.substring(0, result.length() - 1);
        }

        return result;
    }

    public static int getNumericFingerPrint() {
        return new BigInteger(getFingerPrint().replace("-", "").replace("|", ""), 36).intValue();
    }

}
