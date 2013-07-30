package codeOrchestra.colt.core.license;

import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * @author Alexander Eliseyev
 */
public class ActivationReporter {

  private static final String ACTIVATION_URL = "http://activation.codeorchestra.com";

  private String serialNumber;
  
  private HttpClient httpClient = new HttpClient();

  public ActivationReporter(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public boolean report() {
    try {
      PostMethod postMethod = new PostMethod(ACTIVATION_URL);
      postMethod.setParameter("sn", serialNumber);
      postMethod.setParameter("fp", getFingerPrint());
      httpClient.executeMethod(postMethod);
      
      System.out.println("Activation: " + postMethod.getStatusCode());
      
      return true;
    } catch (Throwable t) {
      // ignore
    }
    return false;
  }

  private static String getFingerPrint() {
    StringBuilder resultSB = new StringBuilder();
    try {
      for (final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();) {
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

}
