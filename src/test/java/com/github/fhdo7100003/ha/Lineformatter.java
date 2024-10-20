import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

public class LoggerTest {

    @Test
    public void testLineFormatter() {
        // Arrange
        Logger.LineFormatter formatter = new Logger.LineFormatter();
        Calendar date = Calendar.getInstance();
        String message = "Test log message";

        // Act
        String formatted = formatter.format(date, message, new Object[] { "attr1", "attr2" });

        // Assert
        assertTrue(formatted.contains("Test log message"));
        assertTrue(formatted.contains("attr1=attr2"));
    }
}
