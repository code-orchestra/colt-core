package codeOrchestra.colt.core.logging.model;

/**
 * @author Alexander Eliseyev
 */
public class MessageScope {

  private String name;

  public MessageScope(String name) {
    this.name = name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}