package codeOrchestra.colt.core.license.plimus;

import codeOrchestra.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Alexander Eliseyev
 */
public class PlimusResponse {
  
  private PlimusResponseStatus status;
  
  private int daysTillExpiration = -1;
  private int useCount = -1;
  
  public PlimusResponse(PlimusResponseStatus status, int daysTillExpiration, int useCount) {
    this.status = status;
    this.daysTillExpiration = daysTillExpiration;
    this.useCount = useCount;
  }

  public PlimusResponse(String responseString) {
    Document document = XMLUtils.stringToDOM(responseString);
    Element rootElement = document.getDocumentElement();
    
    NodeList statusElements = rootElement.getElementsByTagName("status");
    if (statusElements != null && statusElements.getLength() > 0) {
      Element statusElement = (Element) statusElements.item(0);
      if (statusElement != null) {
        status = PlimusResponseStatus.valueOf(statusElement.getTextContent());
      }
    }
    
    NodeList daysTillExpirationElements = rootElement.getElementsByTagName("days_till_expiration");
    if (daysTillExpirationElements != null && daysTillExpirationElements.getLength() > 0) {
      Element daysTillExpirationElement = (Element) daysTillExpirationElements.item(0);
      if (daysTillExpirationElement != null) {
        daysTillExpiration = Integer.valueOf(daysTillExpirationElement.getTextContent());
      }
    }
    
    NodeList useCountElements = rootElement.getElementsByTagName("use_count");
    if (useCountElements != null && useCountElements.getLength() > 0) {
      Element useCountElement = (Element) useCountElements.item(0);
      if (useCountElement != null) {
        useCount = Integer.valueOf(useCountElement.getTextContent());
      }
    }
  }

  public PlimusResponseStatus getStatus() {
    return status;
  }

  public int getDaysTillExpiration() {
    return daysTillExpiration;
  }

  public int getUseCount() {
    return useCount;
  }
  
}
