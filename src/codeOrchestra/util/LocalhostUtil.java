package codeOrchestra.util;

import java.net.*;
import java.util.Enumeration;

/**
 * @author Alexander Eliseyev
 */
public class LocalhostUtil {

    public static String getLocalhostIp() {
        try {
            for (final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                final NetworkInterface cur = interfaces.nextElement();
                if (cur.isLoopback()) {
                    continue;
                }

                if (cur.getName().startsWith("vnic")) {
                    // skip parallels virtual interfaces
                    continue;
                }

                for (final InterfaceAddress interfaceAddress : cur.getInterfaceAddresses()) {
                    final InetAddress inetAddress = interfaceAddress.getAddress();
                    if (!((inetAddress instanceof Inet4Address))) {
                        continue;
                    }

                    return inetAddress.getHostAddress();
                }
            }

            return "localhost";
        } catch (Throwable e) {
            return "localhost";
        }
    }

    public static InetAddress getLocalhostAddress() {
        try {
            for (final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                final NetworkInterface cur = interfaces.nextElement();
                if (cur.isLoopback()) {
                    continue;
                }

                for (final InterfaceAddress interfaceAddress : cur.getInterfaceAddresses()) {
                    final InetAddress inetAddress = interfaceAddress.getAddress();
                    if (!((inetAddress instanceof Inet4Address))) {
                        continue;
                    }

                    return inetAddress;
                }
            }

            return null;
        } catch (Exception e) {
            try {
                return InetAddress.getLocalHost();
            } catch (UnknownHostException e1) {
                return null;
            }
        }
    }

}
