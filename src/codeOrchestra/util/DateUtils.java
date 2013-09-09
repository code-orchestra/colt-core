package codeOrchestra.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Alexander Eliseyev
 */
public class DateUtils {
  
  public static final long MILLIS_PER_SECOND = 1000L;
  public static final long MILLIS_PER_MINUTE = 60000L;
  public static final long MILLIS_PER_HOUR = 3600000L;
  public static final long MILLIS_PER_DAY = 86400000L;
  public static final int SEMI_MONTH = 1001;
  public static final int RANGE_WEEK_SUNDAY = 1;
  public static final int RANGE_WEEK_MONDAY = 2;
  public static final int RANGE_WEEK_RELATIVE = 3;
  public static final int RANGE_WEEK_CENTER = 4;
  public static final int RANGE_MONTH_SUNDAY = 5;
  public static final int RANGE_MONTH_MONDAY = 6;

  public static String getCurrentDate() {
      return DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.US).format(new Date());
  }

}
