package codeOrchestra.colt.core.license;

import codeOrchestra.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Alexander Eliseyev
 */
public class UsagePeriods {

    private static final String PERIOD_DELIMITER = "-";
    private static final String PERIOD_SEPARATOR = "|";
    private static String USAGE_PERIODS_KEY = "USAGE_PERIODS_KEY";
    private static int MAX_PAIRS_COUNT = 290;

    private static final UsagePeriods instance = new UsagePeriods();
    public static UsagePeriods getInstance() {
        return instance;
    }

    private final Preferences preferences = Preferences.userNodeForPackage(UsagePeriods.class);

    private List<Pair<Long>> usagePeriods = new ArrayList<>();

    private UsagePeriods() {
        load();
    }

    public void addUsagePeriod(long start, long end) {
        assert end > start;

        if (usagePeriods.size() + 1 > MAX_PAIRS_COUNT) {
            usagePeriods.remove(0);
        }
        usagePeriods.add(new Pair<>(start, end));
        try {
            save();
        } catch (BackingStoreException e) {
            throw new RuntimeException("Can't store the expiration data");
        }
    }

    private void load() {
        String periodsString = preferences.get(USAGE_PERIODS_KEY, "");

        String[] periodsSplit = periodsString.split("\\" + PERIOD_SEPARATOR);
        if (periodsSplit != null && periodsSplit.length > 0) {
            for (String period : periodsSplit) {
                if (period != null && period.contains(PERIOD_DELIMITER)) {
                    String[] periodSplit = period.split("\\" + PERIOD_DELIMITER);
                    long start = Long.valueOf(periodSplit[0]);
                    long end = Long.valueOf(periodSplit[1]);
                    usagePeriods.add(new Pair<>(start, end));
                }
            }
        }
    }

    private void save() throws BackingStoreException {
        StringBuilder sb = new StringBuilder();

        Iterator<Pair<Long>> iterator = usagePeriods.iterator();
        while (iterator.hasNext()) {
            Pair<Long> period = iterator.next();
            sb.append(period.getO1()).append(PERIOD_DELIMITER).append(period.getO2());
            if (iterator.hasNext()) {
                sb.append(PERIOD_SEPARATOR);
            }
        }

        preferences.put(USAGE_PERIODS_KEY, sb.toString());
        preferences.sync();
    }

    public boolean isCurrentTimePresentInUsagePeriods() {
        long currentTime = System.currentTimeMillis();

        for (Pair<Long> usagePeriod : usagePeriods) {
            long start = usagePeriod.getO1();
            long end = usagePeriod.getO2();

            if (currentTime > start && currentTime < end) {
                return true;
            }
        }

        return false;
    }


}
