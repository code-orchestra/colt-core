package codeOrchestra.colt.core.license;

import codeOrchestra.util.FingerprintUtil;
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
      postMethod.setParameter("fp", FingerprintUtil.getFingerPrint());
      httpClient.executeMethod(postMethod);
      
      System.out.println("Activation: " + postMethod.getStatusCode());
      
      return true;
    } catch (Throwable t) {
      // ignore
    }
    return false;
  }

}
