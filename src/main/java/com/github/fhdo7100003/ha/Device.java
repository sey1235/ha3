package com.github.fhdo7100003.ha;

public class Device {
  private String name;
  private Logger logger;

  public Device(String name, Logger logger) {
    this.name = name;
    this.logger = logger;
  }

  public void logEnergy(int value, boolean isProducer) {
    String action = isProducer ? "produced" : "consumed";
    logger.log(name, action, "value", value);
  }
}
