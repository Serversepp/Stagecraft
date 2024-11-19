package de.fh.albsig;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogExampleTest {

    private static final Logger logger = LogManager.getLogger(LogExampleTest.class);
    private TestAppender testAppender;
    private static final String LOG_FILE_PATH = "logs/main.log";

    @BeforeEach
    public void setUp() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        // Create a test appender to collect log events
        testAppender = new TestAppender("TestAppender");
        testAppender.start(); // Start the appender
        config.getRootLogger().addAppender(testAppender, null, null);

        // Configure file appender to log to a specific file
        try (FileWriter writer = new FileWriter(LOG_FILE_PATH, false)) {
            writer.write(""); // Clear existing file content
        } catch (IOException e) {
            e.printStackTrace();
        }

        context.updateLoggers();
    }

    @AfterEach
    public void tearDown() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        config.getRootLogger().removeAppender("TestAppender");
        testAppender.stop(); // Stop the appender
        context.updateLoggers();

        // Truncate the test log file after the test to avoid locking issues
        try (FileWriter writer = new FileWriter(LOG_FILE_PATH, false)) {
            writer.write(""); // Clear existing file content
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoggingToConsoleAndFile() throws IOException {
        // Log messages
        logger.info("This is an info message");
        logger.warn("This is a warning message");
        logger.error("This is an error message");

        // Allow a slight delay for the logging system to process events
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check if the messages are logged to the console using the test appender
        List<LogEvent> events = testAppender.getLogEvents();
        if (events.stream().noneMatch(event -> event.getMessage().getFormattedMessage().equals("This is an info message"))) {
            fail("Info message was not logged to console.");
        }
        if (events.stream().noneMatch(event -> event.getMessage().getFormattedMessage().equals("This is a warning message"))) {
            fail("Warning message was not logged to console.");
        }
        if (events.stream().noneMatch(event -> event.getMessage().getFormattedMessage().equals("This is an error message"))) {
            fail("Error message was not logged to console.");
        }

        // Check if the messages are logged to the file
        List<String> logLines = Files.readAllLines(Path.of(LOG_FILE_PATH));
        if (logLines.stream().noneMatch(line -> line.contains("This is an info message"))) {
            fail("Info message was not logged to file.");
        }
        if (logLines.stream().noneMatch(line -> line.contains("This is a warning message"))) {
            fail("Warning message was not logged to file.");
        }
        if (logLines.stream().noneMatch(line -> line.contains("This is an error message"))) {
            fail("Error message was not logged to file.");
        }
    }

    private static class TestAppender extends AbstractAppender {
        private final List<LogEvent> logEvents = new CopyOnWriteArrayList<>();

        protected TestAppender(String name) {
            super(name, null, PatternLayout.createDefaultLayout(), false, null);
        }

        @Override
        public void append(LogEvent event) {
            logEvents.add(event);
        }

        public List<LogEvent> getLogEvents() {
            return logEvents;
        }
    }
}
