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
      logger.log("Air Fryer 1", "Something happened", "test", 4, "yes", false);
      logger.log("Air Fryer 2", "Something happened", "test", 4, "yes", false);
      logger.log("Air Fryer 1", "Something happened");
      showLogs(logPath, LogFilter.any().from(Calendar.getInstance()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static void showLogs(Path directory, LogFilter filter) throws IOException {
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
