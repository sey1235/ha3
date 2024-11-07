package ha;

import java.io.*;

class DeviceLoggingTest {
    private File logFile;

    public DeviceLoggingTest() throws IOException {
        logFile = new File(System.getProperty("java.io.tmpdir"), "device_log.txt");
        if (!logFile.exists()) logFile.createNewFile();
    }

    public void logEnergy(int amount, boolean isProducing) throws IOException {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            if (isProducing) {
                writer.write("Produced " + amount + " units of energy\n");
            } else {
                writer.write("Consumed " + amount + " units of energy\n");
            }
        }
        System.out.println("Logged " + amount + " units of energy.");
    }

    public void printLogContents() throws IOException {
        System.out.println("Log contents:");
        Files.readAllLines(logFile.toPath()).forEach(System.out::println);
    }

    public void cleanup() {
        if (logFile.exists()) logFile.delete();
    }

    public static void main(String[] args) {
        try {
            DeviceLoggingTest test = new DeviceLoggingTest();
            test.logEnergy(150, true);
            test.logEnergy(100, false);

            test.printLogContents();

            test.cleanup();
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}

