package com.github.fhdo7100003.ha;

import org.junit.jupiter.api.Test; // Importing the Test annotation
import java.util.Calendar;

public class LogMetaTest {

    @Test
    public void testParseValidLogEntry() {
        String logEntry = "2023-10-19_device1.txt"; // Example log entry
        LogMeta logMeta = LogMeta.parse(logEntry);

        // Using fully qualified names for assertions
        org.junit.jupiter.api.Assertions.assertNotNull(logMeta);
        org.junit.jupiter.api.Assertions.assertEquals(2023, logMeta.date.get(Calendar.YEAR));
        org.junit.jupiter.api.Assertions.assertEquals(10, logMeta.date.get(Calendar.MONTH) + 1); // Month is 0-based
        org.junit.jupiter.api.Assertions.assertEquals("device1", logMeta.deviceName);
    }

    @Test
    public void testParseInvalidLogEntry() {
        String invalidLogEntry = "invalid-log-entry.txt"; // Invalid format
        LogMeta logMeta = LogMeta.parse(invalidLogEntry);

        // Using fully qualified names for assertions
        org.junit.jupiter.api.Assertions.assertNull(logMeta); // Should return null for invalid entry
    }
}
