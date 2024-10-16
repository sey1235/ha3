package com.github.fhdo7100003.ha.device;

import java.util.Calendar;

public final class SolarPanel extends Device {
  public SolarPanel(String name) {
    super(name);
  }

  @Override
  protected int innerTick(final Calendar currentTime) {
    // can do some fancy stuff like calculating sun/up down via longitude/latitude
    // but let's just
    // just hardcode it for now
    final var currentHour = currentTime.get(Calendar.HOUR_OF_DAY);

    if (currentHour >= 8 && currentHour <= 18) {
      return 1000;
    } else {
      return 0;
    }
  }
}
