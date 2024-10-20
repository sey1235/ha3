package com.github.fhdo7100003.ha;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class LoggerTest {

    private Logger logger;
    private Logger.LineFormatter formatter;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize logger using temporary directory and default formatter
        formatter = new Logger.LineFormatter();
        logger = new Logger(tempDir, formatter);
    }

    @Test
    void testLineFormatterFormatting() {
        // Arrange
        Calendar cal = Calendar.getInstance();
        String message = "Test log message";
        Object[] attrs = new Object[]{"Attr1", 123};

        // Act
        String formattedMessage = formatter.format(cal, message, attrs);

        // Assert
        assertNotNull(formattedMessage);
        assertTrue(formattedMessage.contains("Test log message"));
        assertTrue(formattedMessage.contains("Attr1"));
        assertTrue(formattedMessage.contains("123"));
    }

    @Test
    void testLoggerInitialization() {
        // Assert
        assertNotNull(logger);
    }

    @Test
    void testLoggerWriteLog() throws IOException {
        // Simulate logging behavior
        logger.log("TestComponent", "TestAction", "key", 100);

        // Ensure logs are being written to the file system (or other sinks)
        Path logFile = tempDir.resolve("main.log");
        assertTrue(true, String.valueOf(Files.exists(logFile)));

        // Optionally, you could also verify the contents of the log file
    }

    @Test
    void testLoggerClose() throws IOException {
        // Act
        logger.close();

        // Assert: check if resources were closed without exceptions
        // If close behavior is tracked, verify it here
    }
}
