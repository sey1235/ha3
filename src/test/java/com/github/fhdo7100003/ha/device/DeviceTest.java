package com.github.fhdo7100003.ha.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.fhdo7100003.ha.device.Device.InvalidDeviceName;

import java.io.IOException;
import java.util.Calendar;

public class DeviceTest {
  @Test
  public void rejectsAbsolutePath() {
    assertThrows(InvalidDeviceName.class, () -> {
      new StableDevice("/test", 5);
    });
  }

  @Test
  public void rejectsRelativePath() {
    assertThrows(InvalidDeviceName.class, () -> {
      new StableDevice("test/test", 5);
    });
  }

  @Test
  public void stableDeviceProducesValue() {
    final var dev = new StableDevice("mydevice", 1000);
    assertEquals(dev.innerTick(Calendar.getInstance()), 1000);
  }

  @Test
  public void solarPanelProducesAtDay() {
    final var dev = new SolarPanel("mypanel");
    final var time = Calendar.getInstance();
    time.set(Calendar.HOUR_OF_DAY, 12);
    assertEquals(dev.innerTick(time), 1000);
  }

  @Test
  public void solarPanelProducesNothingAtNight() {
    final var dev = new SolarPanel("mypanel");
    final var time = Calendar.getInstance();
    time.set(Calendar.HOUR_OF_DAY, 0);
    assertEquals(dev.innerTick(time), 0);
  }

  @Test
  public void storeDoesntOvercharge() {
    final var dev = new Store("battery", 1000, 500);
    assertEquals(dev.tryCharge(1000), 500);
  }

  @Test
  public void storeProducesNothing() {
    final var dev = new Store("battery", 1000, 500);
    assertEquals(dev.innerTick(Calendar.getInstance()), 0);
  }
}
