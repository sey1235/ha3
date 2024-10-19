package com.github.fhdo7100003.ha;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.github.fhdo7100003.ha.Logger.TimestampGenerator;
import com.github.fhdo7100003.ha.device.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class Simulation {
  final Calendar startTime;
  final Calendar endTime;
  final List<Store> stores;
  final List<Device> devices;

  public Simulation(Calendar startTime, Calendar endTime, List<Store> stores, List<Device> devices) {
    if (startTime.compareTo(endTime) >= 0) {
      throw new InvalidSimulation("Nonsensical start/end times");
    }

    this.startTime = startTime;
    this.endTime = endTime;
    this.stores = stores;
    this.devices = devices;
  }

  private static JsonElement forceField(final JsonObject obj, final String field) {
    final var ret = obj.get(field);
    if (ret == null) {
      throw new JsonParseException(String.format("Missing field %s in %s", field, obj));
    }

    return ret;
  }

  private static <T> JsonElement forcePrimitiveField(final JsonObject obj, final String field, final Class<T> c) {
    final var ret = forceField(obj, field);
    final var prim = ret.getAsJsonPrimitive();

    String err = null;
    if (c == String.class) {
      if (!prim.isString()) {
        err = "Expected string";
      }
    } else if (c == Integer.class) {
      if (!prim.isNumber()) {
        err = "Expected integer";
      }
    }

    if (err != null) {
      throw new JsonParseException(String.format("%s for field %s: Got %s", err, field, prim));
    }

    return ret;
  }

  public static Simulation fromJSON(final String json) {
    final var parsed = JsonParser.parseString(json).getAsJsonObject();
    final var startTime = parseRfc3339(parsed.get("startTime").getAsString());
    final var endTime = parseRfc3339(parsed.get("endTime").getAsString());

    final List<Device> devices = new ArrayList<>();
    final List<Store> stores = new ArrayList<>();
    final var deviceArr = Objects.requireNonNullElseGet(forceField(parsed, "devices").getAsJsonArray(),
        () -> new JsonArray());
    for (final var device : deviceArr) {
      final var deviceObj = device.getAsJsonObject();

      final var name = forcePrimitiveField(deviceObj, "name", String.class).getAsString();
      final var type = forcePrimitiveField(deviceObj, "type", String.class).getAsString();
      switch (type) {
        case "StableDevice":
          devices.add(new StableDevice(name, forcePrimitiveField(deviceObj, "produces", Integer.class).getAsInt()));
          break;
        case "Store":
          final var capacity = forcePrimitiveField(deviceObj, "maxCapacity", Integer.class).getAsInt();
          final var chargePerTick = forcePrimitiveField(deviceObj, "maxChargePerTick", Integer.class).getAsInt();
          stores.add(new Store(name, capacity, chargePerTick));
          break;
        case "SolarPanel":
          devices.add(new SolarPanel(name));
          break;
        default:
          throw new JsonParseException(String.format("Unknown type %s", type));
      }
    }

    return new Simulation(startTime, endTime, stores, devices);
  }

  public static Simulation fromPath(final Path path) throws IOException {
    return fromJSON(Files.readString(path));
  }

  public static class StaticTimestampGenerator implements TimestampGenerator {
    private Calendar currentTime;

    public void setCurrentTime(Calendar currentTime) {
      this.currentTime = currentTime;
    }

    @Override
    public Calendar now() {
      return currentTime;
    }

  }

  public Report run(final Logger logger) {
    final var currentTime = (Calendar) startTime.clone();
    long result = 0;

    Consumer<Calendar> setTimestamp = (_timestamp) -> {
      return;
    };
    final var gen = logger.getTimestampGenerator();
    if (gen instanceof StaticTimestampGenerator sgen) {
      setTimestamp = (timestamp) -> sgen.setCurrentTime(timestamp);
    }

    while (currentTime.compareTo(endTime) < 0) {
      setTimestamp.accept(currentTime);

      var consumed = devices.stream().map(dev -> dev.tick(currentTime, logger)).reduce((a, b) -> a + b).get();

      final var it = stores.iterator();
      while (consumed != 0 && it.hasNext()) {
        final var store = it.next();
        consumed = consumed - store.tryCharge(consumed);
      }

      if (consumed < 0) {
        logger.log("Drained from energy grid", "amount", consumed);
      } else {
        logger.log("Added to energy grid", "amount", consumed);
      }

      result += consumed;

      currentTime.add(Calendar.HOUR_OF_DAY, 1);
    }

    return new Report(result);
  }

  // TODO: add more interesting fields
  public static record Report(long result) {
  }

  private static Calendar parseRfc3339(final String s) {
    // this time API is a mess
    final var parsed = Instant.parse(s);
    final var dt = ZonedDateTime.ofInstant(parsed, ZoneId.systemDefault());
    return GregorianCalendar.from(dt);
  }

  public static class InvalidSimulation extends RuntimeException {
    public InvalidSimulation(final String msg) {
      super(msg);
    }
  }
}
