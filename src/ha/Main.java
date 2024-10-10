package ha;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import ha.LogMeta.LogFilter;

public class Main {
  public static void main(String[] args) {
    final var logPath = Path.of("./log");
    try (var logger = new Logger(logPath)) {
      Device device1 = new Device("Solar Panel", logger);
      Device device2 = new Device("Air Conditioner", logger);
      device1.logEnergy(150, true); // Producing energy
      device2.logEnergy(100, false); // Consuming energy

      showLogs(logPath, LogFilter.any().from(Calendar.getInstance()));
    } catch (IOException e) {
      e.printStackTrace();
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
