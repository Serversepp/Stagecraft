package de.fh.albsig;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for creating and displaying the main menu with scrollable buttons.
 */
public class MainMenu {

    private static final Logger logger = LogManager.getLogger(MainMenu.class);

    private final Stage stage;
    private UiFx uiFx; // Not final bc testing

    /**
     * Constructor for MainMenu.
     *
     * @param stage the primary stage
     */
    public MainMenu(Stage stage) {
        this.stage = stage;
        this.uiFx = new UiFx();
    }

    /**
     * Constructor for MainMenu with custom UiFx (for testing).
     *
     * @param stage the primary stage
     * @param uiFx  the UiFx instance to use
     */
    public MainMenu(Stage stage, UiFx uiFx) {
        this.stage = stage;
        this.uiFx = uiFx;
    }

    /**
     * Sets the UiFx instance, used for testing or custom behavior.
     *
     * @param uiFx the UiFx instance to set
     */
    public void setUiFx(UiFx uiFx) {
        this.uiFx = uiFx;
    }

    /**
     * Sets up and displays the main menu.
     */
    public void setupAndShow() {
        logger.info("Setting up the main menu.");

        // Main layout
        VBox mainLayout = new VBox(10); // 10px spacing
        mainLayout.setPrefWidth(800);
        mainLayout.setPrefHeight(600);

        // Title label
        uiFx.addLabel(mainLayout, "Welcome to the Main Menu!");

        // Scrollable button container
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox buttonContainer = new VBox(10); // 10px spacing between buttons
        buttonContainer.setId("buttonContainer"); // Assign fx:id for testing
        scrollPane.setContent(buttonContainer);

        // Content area for screens
        StackPane contentArea = new StackPane();
        uiFx.addLabel(new VBox(contentArea), "Select a screen by clicking a button.");

        // Add function buttons
        addFunctionButton(buttonContainer, "Ohm Calculator", "/screens/ohmCalc.fxml", contentArea);
        addFunctionButton(buttonContainer, "Screen 2", "/screens/Screen2.fxml", contentArea);
        addFunctionButton(buttonContainer, "Screen 3", "/screens/Screen3.fxml", contentArea);

        // Add components to main layout
        mainLayout.getChildren().addAll(scrollPane, contentArea);

        // Set up the stage
        Scene scene = new Scene(mainLayout, 800, 600);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();

        logger.info("Main menu displayed.");
    }

    /**
     * Adds a button for a specific function screen to the button container.
     *
     * @param buttonContainer the container for the buttons
     * @param buttonText      the text to display on the button
     * @param fxmlPath        the path to the FXML file for the screen
     * @param contentArea     the content area where the screen will be loaded
     */
    private void addFunctionButton(VBox buttonContainer, String buttonText, String fxmlPath, StackPane contentArea) {
        logger.info("Adding button for: {}", buttonText);

        Button button = new Button(buttonText);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> {
            logger.info("Button clicked: {}", buttonText);
            uiFx.loadScreen(contentArea, fxmlPath);
        });
        buttonContainer.getChildren().add(button);
    }

}
