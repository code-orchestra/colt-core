package codeOrchestra.colt.core.license;

import codeOrchestra.colt.core.license.strategy.PlimusSubscriptionWithDemoExpirationStrategy;

/**
 * @author Alexander Eliseyev
 */
public final class ExpirationHelper {

  public static ExpirationStrategy getExpirationStrategy() {
    return expirationStrategy;
  }

  private static final ExpirationStrategy expirationStrategy = new PlimusSubscriptionWithDemoExpirationStrategy();
}
