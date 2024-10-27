package com.github.fhdo7100003.ha;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.github.fhdo7100003.ha.LogMeta.LogFilter;
import com.github.fhdo7100003.ha.Logger.LineFormatter;
import com.github.fhdo7100003.ha.Simulation.Report;
import com.github.fhdo7100003.ha.Simulation.StaticTimestampGenerator;

public class Main {
  public static void main(String[] args) {
    final var logPath = Path.of("./log");
    try (var logger = Logger.open(logPath, new LineFormatter(), new StaticTimestampGenerator())) {
      final var sim = Simulation.fromPath(Path.of("./example_simulation.json"));
      final var res = sim.run(logger);

      System.out.printf("End result of simulation: %sWh %s\n", Math.abs(res.result()),
          res.result() < 0 ? "consumed from power grid" : "added to power grid");

      // showLogs(logPath, LogFilter.any().from(Calendar.getInstance()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // this sucks
  static class Result<T, E> {
    T res;
    E err;

    private Result(T res, E err) {
      this.res = res;
      this.err = err;
    }

    public static <T, E> Result<T, E> ok(T res) {
      return new Result<T, E>(res, null);
    }

    public static <T, E> Result<T, E> err(E err) {
      return new Result<T, E>(null, err);
    }
  }

  static record SimulationRunner(Path logRoot) {
    CompletableFuture<Result<Report, IOException>> runSimulation(final Simulation sim) {
      final var ret = new CompletableFuture<Result<Report, IOException>>();
      Thread.ofPlatform().start(() -> {
        final var id = UUID.randomUUID();
        final var gen = new StaticTimestampGenerator();
        try (final var logger = Logger.open(logRoot.resolve(id.toString()), new LineFormatter(), gen)) {
          final var res = sim.run(logger);
          System.out.printf("End result of simulation: %sWh %s\n", Math.abs(res.result()),
              res.result() < 0 ? "consumed from power grid" : "added to power grid");

          ret.complete(Result.ok(res));
        } catch (IOException e) {
          ret.complete(Result.err(e));
        }
      });
      return ret;
    }
  }

  static void showLogs(final Path directory, final LogFilter filter) throws IOException {
    try (var list = Files.list(directory)) {
      final var it = list.iterator();
      while (it.hasNext()) {
        final var path = it.next();
        final var fileName = path.getFileName().toString();
        final var meta = LogMeta.parse(fileName);
        if (meta != null && filter.matches(meta)) {
          final var content = Files.readString(path);
          System.out.printf("Log file from %s\n%s", Logger.YMD.format(meta.date().getTime()),
              content);
        }
      }
    }
  }
}
