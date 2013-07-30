package codeOrchestra.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author Alexander Eliseyev
 */
public class LocalhostUtil {

  public static String getLocalhostIp() {
    try {
      for (final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();) {
        final NetworkInterface cur = (NetworkInterface) interfaces.nextElement();
        if (cur.isLoopback()) {
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

      return null;
    } catch (Exception e) {
      return "localhost";
    }
  }

}
