package de.fh.albsig;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point for the JavaFX application.
 */
public class Main extends Application {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) {
        LOGGER.info("Starting JavaFX application.");

        try {
            MainMenu mainMenu = new MainMenu(primaryStage);
            mainMenu.setupAndShow();
        } catch (Exception e) {
            LOGGER.error("An error occurred while initializing the application.", e);
        }
    }

    /**
     * Main method to launch the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        LOGGER.info("Launching the JavaFX application.");
        Application.launch(args);
        LOGGER.info("Application exited.");
    }
}
