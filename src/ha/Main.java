package ha;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
  public static void main(String[] args) {
    try (var logger = new Logger(Path.of("./log"))) {
      logger.log("something", "???", "test", 4, "yes", false);
      logger.log("something", "???", "test", 4);
      logger.log("something", "???", "test", 4);
      logger.log("something", "???", "test", 4);
      logger.log("something else", "yes", "test", 5);
    } catch (IOException e) {
      System.out.println("Failed creating logger");
    }
  }
}
