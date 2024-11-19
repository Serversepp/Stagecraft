package de.fh.albsig;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class to launch the JavaFX application.
 */
public class Main extends Application {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) {
        LOGGER.info("Initializing the UI.");

        // Create a UiFx object with the primaryStage
        UiFx ui = new UiFx(primaryStage);

        // Set up the user interface
        ui.setupUi();

        // Display the user interface
        ui.show();

        LOGGER.info("UI setup completed and displayed.");
    }

    /**
     * Main method to launch the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        LOGGER.info("Launching JavaFX Application.");
        Application.launch(args);
    }
}
