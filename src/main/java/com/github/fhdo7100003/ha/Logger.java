package com.github.fhdo7100003.ha;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;

public final class Logger implements Closeable {
  final private Path directory;
  final private Map<String, Entry> sinks;
  final private LoggingSink mainLog;
  final private Formatter fmt;
  final public static SimpleDateFormat YMD = new SimpleDateFormat("yyyy-MM-dd");

  public static interface Formatter {
    String format(final Calendar date, final String message, final Object[] attrs);
  }

  public static final class LineFormatter implements Formatter {
    final private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

    public String format(final Calendar date, final String message, final Object[] attrs) {
      final var buf = new StringBuilder();
      buf.append(dateFormat.format(date.getTime()));
      buf.append(": ");
      buf.append(message);
      if (attrs.length > 0) {
        buf.append(" ");
      }

      boolean writeEq = true;
      for (int i = 0; i < attrs.length; i++) {
        final var attr = attrs[i];
        buf.append(attr.toString().replace("\n", " "));
        if (writeEq && !(i == attrs.length - 1)) {
          buf.append("=");
        } else if (i < attrs.length - 1) {
          buf.append(",");
        }
        writeEq = !writeEq;
      }

      buf.append("\n");

      return buf.toString();
    }
  }

  private static record Entry(LoggingSink sink, int date) {
  }

  public Logger(final Path directory) throws IOException {
    this(directory, new LineFormatter());
  }

  public Logger(final Path directory, final Formatter fmt) throws IOException {
    this.directory = directory;
    Files.createDirectories(directory);
    this.fmt = fmt;
    this.mainLog = LoggingSink.open(directory.resolve("all.txt"));
    this.sinks = new HashMap<>();
  }

  private Path getPath(final Calendar date, final String deviceName) {
    return directory.resolve(String.format("%s_%s.txt", YMD.format(date.getTime()), deviceName));
  }

  public void log(final String deviceName, final String message, Object... attrs) {
    final var now = Calendar.getInstance();
    Entry entry = sinks.get(deviceName);
    final var today = now.get(Calendar.DAY_OF_YEAR);
    LoggingSink sink;
    if (entry == null || today != entry.date) {
      final var entryPath = getPath(now, deviceName);
      try {
        Entry oldEntry;
        sink = LoggingSink.open(entryPath);
        if ((oldEntry = sinks.put(deviceName, new Entry(sink, today))) != null) {
          oldEntry.sink.close();
        }
      } catch (IOException e) {
        System.out.printf("Failed opening %s: %s\n", entryPath, e);
        return;
      }
    } else {
      sink = entry.sink;
    }

    final var buf = fmt.format(now, message, attrs);
    sink.writeEntry(buf);

    mainLog.writeEntry(buf);
  }

  @Override
  public void close() throws IOException {
    for (Entry v : sinks.values()) {
      try {
        v.sink.close();
      } catch (Exception e) {
        System.out.printf("Failed closing output %s\n: %s\n", v.sink.getPath(), e);
      }
    }
  }
}
