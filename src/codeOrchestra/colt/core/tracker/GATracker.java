package codeOrchestra.colt.core.tracker;

import com.dmurph.tracking.AnalyticsConfigData;
import com.dmurph.tracking.JGoogleAnalyticsTracker;
import com.dmurph.tracking.VisitorData;
import com.dmurph.tracking.system.AWTSystemPopulator;

/**
 * @author Dima Kruk
 */
public class GATracker {
    private static GATracker ourInstance = new GATracker();

    private JGoogleAnalyticsTracker tracker;

    public static GATracker getInstance() {
        return ourInstance;
    }

    private GATracker() {
        JGoogleAnalyticsTracker.setProxy(System.getenv("http_proxy"));
        int UUID = 1;//TODO: make unique ID
        VisitorData visitorData = VisitorData.newVisitor(UUID);
        //TODO: replace with production TrackingCode
        AnalyticsConfigData config = new AnalyticsConfigData("UA-42969501-3", visitorData);
        AWTSystemPopulator.populateConfigData(config);

        tracker = new JGoogleAnalyticsTracker(config, JGoogleAnalyticsTracker.GoogleAnalyticsVersion.V_4_7_2);
    }

    public JGoogleAnalyticsTracker getTracker() {
        return tracker;
    }
}
