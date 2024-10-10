package ha;

import java.text.ParseException;
import java.util.Calendar;
import java.util.regex.Pattern;

public record LogMeta(Calendar date, String deviceName) {

  final private static Pattern REGEX = Pattern.compile("(.+-.+-.+)_(.+)\\.txt");

  public static LogMeta parse(String s) {
    final var matcher = REGEX.matcher(s);
    if (matcher.matches()) {
      try {
        // terrible API
        final var date = Logger.YMD.parse(matcher.group(1));

        final var cal = Calendar.getInstance();
        cal.setTime(date);
        return new LogMeta(cal, matcher.group(2));
      } catch (ParseException e) {
      }
    }
    return null;
  }

  public static final class LogFilter {
    private Calendar from;
    private String deviceName;

    public LogFilter from(final Calendar from) {
      this.from = from;
      return this;
    }

    public LogFilter name(final String deviceName) {
      this.deviceName = deviceName;
      return this;
    }

    public static LogFilter any() {
      return new LogFilter();
    }

    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
      if (cal1 == null || cal2 == null)
        return false;
      return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
          && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
          && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public boolean matches(final LogMeta meta) {
      boolean matches = true;
      // could be prettier
      if (from != null) {
        matches = isSameDay(meta.date, from);
      }

      if (matches && deviceName != null) {
        matches = meta.deviceName.equals(deviceName);
      }

      return matches;
    }
  }
}

