package com.github.fhdo7100003.ha;

import org.junit.jupiter.api.Test; // Importing the Test annotation

import com.github.fhdo7100003.ha.LogMeta.LogFilter;

import java.util.Calendar;

public class LogfilterTest {
  @Test
  public void testMatchesWithSameDayAndDeviceName() {
    Calendar date1 = Calendar.getInstance();
    date1.set(2023, Calendar.OCTOBER, 19);

    LogMeta logMeta = new LogMeta(date1, "device1");
    LogFilter filter = LogFilter.any().from(date1).name("device1");

    // Using fully qualified names for assertions
    org.junit.jupiter.api.Assertions.assertTrue(filter.matches(logMeta)); // Should match
  }

  @Test
  public void testMatchesWithDifferentDeviceName() {
    Calendar date1 = Calendar.getInstance();
    date1.set(2023, Calendar.OCTOBER, 19);

    LogMeta logMeta = new LogMeta(date1, "device1");
    LogFilter filter = LogFilter.any().from(date1).name("device2");

    // Using fully qualified names for assertions
    org.junit.jupiter.api.Assertions.assertFalse(filter.matches(logMeta)); // Should not match due to different device
                                                                           // names
  }

  @Test
  public void testMatchesWithDifferentDate() {
    Calendar date1 = Calendar.getInstance();
    date1.set(2023, Calendar.OCTOBER, 19);

    Calendar date2 = Calendar.getInstance();
    date2.set(2023, Calendar.OCTOBER, 20);

    LogMeta logMeta = new LogMeta(date1, "device1");
    LogFilter filter = LogFilter.any().from(date2);

    // Using fully qualified names for assertions
    org.junit.jupiter.api.Assertions.assertFalse(filter.matches(logMeta)); // Should not match due to different dates
  }
}
