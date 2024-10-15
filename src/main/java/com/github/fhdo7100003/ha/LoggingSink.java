package com.github.fhdo7100003.ha;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class LoggingSink implements Closeable {
  OutputStream fd;
  Path path;
  boolean writable;

  private LoggingSink(final Path path) throws IOException {
    this.path = path;
    fd = openAppend(path);
    writable = true;
  }

  static public LoggingSink open(final Path path) throws IOException {
    return new LoggingSink(path);
  }

  public Path getPath() {
    return path;
  }

  private static OutputStream openAppend(final Path path) throws IOException {
    return Files.newOutputStream(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
  }

  public void delete() throws IOException {
    writable = false;
    fd.close();
    Files.delete(path);
  }

  public void move(final Path destination) throws IOException {
    writable = false;
    fd.close();
    Files.move(path, destination);
    fd = openAppend(path);
    writable = true;
  }

  public void archive(final Path destination) throws IOException {
    writable = false;
    fd.close();
    Files.move(path, destination);
  }

  public void writeEntry(final String entry) {
    assert writable;
    assert entry.endsWith("\n");
    final var buf = StandardCharsets.UTF_8.encode(entry);
    // TODO: should probably just wrap the OutputStream into a channel so this isn't
    // called for no reason
    final var chan = Channels.newChannel(fd);
    try {
      chan.write(buf);
      fd.flush();
    } catch (IOException e) {
      System.out.printf("Failed writing to %s: %s\n", path, e);
    }
  }

  @Override
  public void close() throws IOException {
    fd.close();
  }
}
