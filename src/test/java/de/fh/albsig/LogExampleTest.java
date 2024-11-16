package de.fh.albsig;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

class LogExample {
    private static final Logger logger = LogManager.getLogger(LogExample.class);

    public void doSomething() {
        logger.info("This is an info message");
        logger.error("This is an error message");
    }
}

class LogExampleTest {
    private LogExample logExample;
    private TestAppender testAppender;

    @BeforeEach
    void setUp() {
        logExample = new LogExample();
        testAppender = new TestAppender();
        testAppender.start();

        // Use LoggerContext with try-with-resources
        try (org.apache.logging.log4j.core.LoggerContext context =
                     (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false)) {
            context.setConfigLocation(new java.net.URI("log4j2.xml"));
            LoggerConfig loggerConfig = context.getConfiguration()
                    .getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
            loggerConfig.addAppender(testAppender, null, null);
            context.updateLoggers(); // Apply the configuration changes
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure logger", e);
        }
    }


    @AfterEach
    void tearDown() {
        testAppender.stop();
    }

    @Test
    void testLogging() {
        logExample.doSomething();

        // Überprüfen, ob bestimmte Nachrichten geloggt wurden
        List<String> logMessages = testAppender.getMessages();
        assertTrue(logMessages.contains("This is an info message"), "Info-Nachricht fehlt");
        assertTrue(logMessages.contains("This is an error message"), "Error-Nachricht fehlt");
    }

    // Custom Appender zum Abfangen der Logs
    private static class TestAppender extends AbstractAppender {
        private final List<String> messages = new ArrayList<>();

        protected TestAppender() {
            super("TestAppender", null, null, true, null);
        }

        @Override
        public void append(LogEvent event) {
            messages.add(event.getMessage().getFormattedMessage());
        }

        public List<String> getMessages() {
            return messages;
        }
    }
}
