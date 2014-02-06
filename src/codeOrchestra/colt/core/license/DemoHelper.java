package codeOrchestra.colt.core.license;

import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;

/**
 * @author Alexander Eliseyev
 */
public final class DemoHelper {

    private static DemoHelper demoHelper = new DemoHelper();

    public static final DemoHelper get() {
        return demoHelper;
    }

    private DemoHelper() {
    }

    private int compilationsCount = 1;

    public int getMaxCompilationsCount() {
        return LiveCodingHandlerManager.getInstance().getCurrentHandler().getDemoModeMaxUpdatesCount();
    }

    public void incrementCompilationsCount() {
        compilationsCount++;
    }

    public boolean maxCompilationsCountReached() {
        if (!isInDemoMode()) {
            return false;
        }
        return compilationsCount >= getMaxCompilationsCount();
    }

    public void reset() {
        compilationsCount = 1;
    }

    public boolean isInDemoMode() {
        return ExpirationHelper.getExpirationStrategy().allowsDemo() && ExpirationHelper.getExpirationStrategy().isInDemoMode();
    }

}
