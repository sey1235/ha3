public class Logger {
    public static void log(String message) {
        System.out.println("Log: " + message);
    }
}

public class Device {
    private String name;
    private Logger logger;
    
    public Device(String name, Logger logger) {
        this.name = name;
        this.logger = logger;
    }

    public void logEnergy(int value, boolean isProducer) {
        String action = isProducer ? "produced" : "consumed";
        logger.log(name + " " + action + " " + value + " units of energy.");
    }
}

public class Main {
    public static void main(String[] args) {
        Logger logger = new Logger();
        Device device1 = new Device("Solar Panel", logger);
        Device device2 = new Device("Air Conditioner", logger);

        device1.logEnergy(150, true); // Producing energy
        device2.logEnergy(100, false); // Consuming energy
    }
}
