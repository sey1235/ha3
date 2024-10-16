package com.github.fhdo7100003.ha.device;

import java.util.Calendar;

public final class StableDevice extends Device {
  final private int produces;

  public StableDevice(final String name, final int produces) {
    super(name);
    this.produces = produces;
  }

  @Override
  protected int innerTick(final Calendar currentTime) {
    return produces;
  }
}
