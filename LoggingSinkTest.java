package com.github.fhdo7100003.ha;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class LoggingSinkTest {
  @Test
  public void createsNonExistantFile(@TempDir final Path tmp) throws IOException {
    final var filePath = tmp.resolve("something.txt");
    try (final var _sink = LoggingSink.open(filePath)) {
      assertTrue(Files.exists(filePath));
    }
  }
}
