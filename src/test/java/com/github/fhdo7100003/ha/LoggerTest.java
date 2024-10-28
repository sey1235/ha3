package com.github.fhdo7100003.ha;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoggerTest {

  @Test
  public void testLogWritesToMainLog(@TempDir Path tempDir) throws IOException {
    // Arrange
    Logger.Formatter formatter = new Logger.LineFormatter();
    Logger.TimestampGenerator generator = new Logger.RealtimeGenerator();
    Path mainLogFile = tempDir.resolve("all.txt");
    Logger logger = Logger.open(tempDir, formatter, generator);

    // Act
    logger.log("Test message");

    // NOTE: logging is completely async so this is racy, just wait
    // TODO: maybe make ensureFlushed method
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    // Assert
    assertTrue(Files.exists(mainLogFile));
    String content = Files.readString(mainLogFile);
    assertTrue(content.contains("Test message"));

    // Cleanup
    logger.close();
  }
}
