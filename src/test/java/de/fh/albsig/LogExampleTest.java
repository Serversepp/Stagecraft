package de.fh.albsig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the Log4j2 configuration for console and file outputs.
 */
class Log4j2ConfigTest {

    private static final Logger TEST_LOGGER = LogManager.getLogger(Log4j2ConfigTest.class);
    private static final File LOG_FILE = new File("logs/application.log");
    private static StringWriter consoleOutput;

    /**
     * Sets up the file and console appender before all tests.
     *
     * @throws IOException if the log file cannot be created.
     */
    @BeforeAll
    static void setUpBeforeAll() throws IOException {

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("%d{yyyy-MM-dd HH:mm:ss} [%t] %-5level %logger{36} - %msg%n")
                .build();

        consoleOutput = new StringWriter();
        WriterAppender writerAppender = WriterAppender.newBuilder()
                .setName("TestConsoleAppender")
                .setTarget(consoleOutput)
                .setLayout(layout)
                .build();
        writerAppender.start();

        LoggerConfig loggerConfig = config.getLoggerConfig(Log4j2ConfigTest.class.getName());
        loggerConfig.addAppender(writerAppender, null, null);
        context.updateLoggers();
    }

    /**
     * Cleans up the log file after all tests.
     */
    @AfterAll
    static void tearDownAfterAll() {
        if (LOG_FILE.exists()) {
            LOG_FILE.delete();
        }
    }

    @Test
    void testTraceNotLoggedToFile() throws IOException {
        TEST_LOGGER.trace("This is a TRACE message");
        waitForLogsToBeWritten();
        assertFileDoesNotContain("This is a TRACE message");
    }

    @Test
    void testDebugNotLoggedToFile() throws IOException {
        TEST_LOGGER.debug("This is a DEBUG message");
        waitForLogsToBeWritten();
        assertFileDoesNotContain("This is a DEBUG message");
    }

    @Test
    void testInfoNotLoggedToFile() throws IOException {
        TEST_LOGGER.info("This is an INFO message");
        waitForLogsToBeWritten();
        assertFileDoesNotContain("This is an INFO message");
    }

    @Test
    void testWarnNotLoggedToFile() throws IOException {
        TEST_LOGGER.warn("This is a WARN message");
        waitForLogsToBeWritten();
        assertFileDoesNotContain("This is a WARN message");
    }

    @Test
    void testErrorLoggedToFile() throws IOException {
        TEST_LOGGER.error("This is an ERROR message");
        waitForLogsToBeWritten();
        assertFileContains("This is an ERROR message");
    }

    @Test
    void testFatalLoggedToFile() throws IOException {
        TEST_LOGGER.fatal("This is a FATAL message");
        waitForLogsToBeWritten();
        assertFileContains("This is a FATAL message");
    }

    @Test
    void testTraceNotLoggedToConsole() {
        TEST_LOGGER.trace("This is a TRACE message");
        assertConsoleDoesNotContain("This is a TRACE message");
    }

    @Test
    void testDebugNotLoggedToConsole() {
        TEST_LOGGER.debug("This is a DEBUG message");
        assertConsoleDoesNotContain("This is a DEBUG message");
    }

    @Test
    void testInfoLoggedToConsole() {
        TEST_LOGGER.info("This is an INFO message");
        assertConsoleContains("This is an INFO message");
    }

    @Test
    void testWarnLoggedToConsole() {
        TEST_LOGGER.warn("This is a WARN message");
        assertConsoleContains("This is a WARN message");
    }

    @Test
    void testErrorLoggedToConsole() {
        TEST_LOGGER.error("This is an ERROR message");
        assertConsoleContains("This is an ERROR message");
    }

    @Test
    void testFatalLoggedToConsole() {
        TEST_LOGGER.fatal("This is a FATAL message");
        assertConsoleContains("This is a FATAL message");
    }

    /**
     * Waits briefly to ensure asynchronous logs are written.
     */
    private void waitForLogsToBeWritten() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Asserts that the log file contains the expected message.
     *
     * @param expectedMessage the message to check for.
     * @throws IOException if the log file cannot be read.
     */
    private void assertFileContains(String expectedMessage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            StringBuilder logFileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                logFileContent.append(line);
            }
            assertTrue(logFileContent.toString().contains(expectedMessage),
                    "Log file should contain: " + expectedMessage);
        }
    }

    /**
     * Asserts that the log file does not contain the unexpected message.
     *
     * @param unexpectedMessage the message to check for.
     * @throws IOException if the log file cannot be read.
     */
    private void assertFileDoesNotContain(String unexpectedMessage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            StringBuilder logFileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                logFileContent.append(line);
            }
            assertFalse(logFileContent.toString().contains(unexpectedMessage),
                    "Log file should not contain: " + unexpectedMessage);
        }
    }

    /**
     * Asserts that the console output contains the expected message.
     *
     * @param expectedMessage the message to check for.
     */
    private void assertConsoleContains(String expectedMessage) {
        assertTrue(consoleOutput.toString().contains(expectedMessage),
                "Console log should contain: " + expectedMessage);
    }

    /**
     * Asserts that the console output does not contain the unexpected message.
     *
     * @param unexpectedMessage the message to check for.
     */
    private void assertConsoleDoesNotContain(String unexpectedMessage) {
        assertFalse(consoleOutput.toString().contains(unexpectedMessage),
                "Console log should not contain: " + unexpectedMessage);
    }
}
