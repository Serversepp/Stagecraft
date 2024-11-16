package de.fh.albsig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The {@code Main} class serves as the entry point of the application.
 * It initializes logging using Log4j and logs the application start event.
 */

public class Main {
    // Initialize the logger for the Main class
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * The main method that serves as the entry point for the application.
     *
     * <p>Logs an informational message indicating that the application has started.</p>
     *
     * @param args command-line arguments passed to the program (not used in this application)
     */

    public static void main(String[] args) {
        logger.info("Application started.");
    }
}