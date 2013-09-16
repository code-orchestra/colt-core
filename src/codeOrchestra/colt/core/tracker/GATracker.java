package codeOrchestra.colt.core.tracker;

import codeOrchestra.util.FingerprintUtil;
import codeOrchestra.util.StringUtils;
import com.dmurph.tracking.AnalyticsConfigData;
import com.dmurph.tracking.AnalyticsRequestData;
import com.dmurph.tracking.JGoogleAnalyticsTracker;
import com.dmurph.tracking.VisitorData;
import com.dmurph.tracking.system.AWTSystemPopulator;

/**
 * @author Dima Kruk
 */
public class GATracker {
    private static GATracker ourInstance = new GATracker();

    private JGoogleAnalyticsTracker tracker;

    private final String hostName = "codeorchestra.com";
    private String prevPage = "/";
    private String prevPageTitle = "";


    public static GATracker getInstance() {
        return ourInstance;
    }

    private GATracker() {
        JGoogleAnalyticsTracker.setProxy(System.getenv("http_proxy"));
        int UUID = FingerprintUtil.getNumericFingerPrint();
        VisitorData visitorData = VisitorData.newVisitor(UUID);
        String gaCode = System.getProperty("ga.code");
        if (StringUtils.isEmpty(gaCode)) {
            gaCode = "UA-40699654-3";
        }
//        System.out.println("gaCode = " + gaCode);
        AnalyticsConfigData config = new AnalyticsConfigData(gaCode, visitorData);
        AWTSystemPopulator.populateConfigData(config);

        tracker = new JGoogleAnalyticsTracker(config, JGoogleAnalyticsTracker.GoogleAnalyticsVersion.V_4_7_2);
    }

    public void trackEvent(String argCategory, String argAction) {
        tracker.trackEvent(argCategory, argAction);
    }

    public void trackEventWithPage(String argCategory, String argAction) {
        AnalyticsRequestData data = new AnalyticsRequestData();
        data.setEventCategory(argCategory);
        data.setEventAction(argAction);
        data.setEventLabel(null);
        data.setEventValue(null);
        data.setPageTitle(prevPageTitle);
        data.setPageURL(prevPage);

        tracker.makeCustomRequest(data);
    }

    public void trackPageView(String argPageURL, String argPageTitle) {
        tracker.trackPageViewFromReferrer(argPageURL, argPageTitle, hostName, hostName, prevPage);
        prevPage = argPageURL;
        prevPageTitle = argPageTitle;
    }
}
