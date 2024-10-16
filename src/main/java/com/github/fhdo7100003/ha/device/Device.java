package com.github.fhdo7100003.ha.device;

import java.util.Calendar;

import com.github.fhdo7100003.ha.Logger;

public abstract class Device {
  final private String name;

  public String getName() {
    return name;
  }

  public Device(String name) {
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
}
