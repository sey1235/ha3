package com.github.fhdo7100003.ha.device;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Calendar;

import com.github.fhdo7100003.ha.Logger;

public abstract class Device {
  final private String name;

  public String getName() {
    return name;
  }

  public Device(String name) {
    try {
      // only allow names viable in filenames
      Path.of(name);
    } catch (InvalidPathException e) {
      throw new InvalidDeviceName(String.format("Invalid device name %s, not usable in filenames"), e);
    }
    this.name = name;
  }

  public final int tick(final Calendar currentTime, final Logger logger) {
    final var v = innerTick(currentTime);
    if (v != 0) {
      logger.log(name, v < 0 ? "consumed" : "produced", "value", v);
    }
    return v;
  }

  protected abstract int innerTick(final Calendar currentTime);

  public static class InvalidDeviceName extends RuntimeException {
    public InvalidDeviceName(final String msg, final Throwable err) {
      super(msg, err);
    }
  }
}
