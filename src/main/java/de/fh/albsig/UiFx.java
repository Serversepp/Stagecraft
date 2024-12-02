package de.fh.albsig;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * A utility class for JavaFX user interface operations.
 * This class provides methods to add UI components, display alerts,
 * and load FXML screens.
 */

public class UiFx {

    private static final Logger LOGGER = LogManager.getLogger(UiFx.class);

    /**
     * Adds a label with the specified text to the provided VBox layout.
     *
     * @param layout the VBox layout to which the label will be added
     * @param text the text to display on the label
     */
    public void addLabel(VBox layout, String text) {
        LOGGER.info("Adding label with text: {}", text);
        Label label = new Label(text);
        layout.getChildren().add(label);
    }

    /**
     * Adds a button with the specified text to the provided VBox layout.
     * The button triggers the provided action when clicked.
     *
     * @param layout the VBox layout to which the button will be added
     * @param text the text to display on the button
     * @param onClick the action to execute when the button is clicked
     */
    public void addButton(VBox layout, String text, Runnable onClick) {
        LOGGER.info("Adding button with text: {}", text);
        Button button = new Button(text);
        button.setOnAction(event -> {
            LOGGER.debug("Button clicked: {}", text);
            onClick.run();
        });
        layout.getChildren().add(button);
    }

    /**
     * Displays an informational alert with the specified message.
     *
     * @param message the message to display in the alert
     */
    public void showAlert(String message) {
        LOGGER.warn("Showing alert: {}", message);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Loads an FXML screen from the specified path and sets it as the content
     * of the provided StackPane.
     *
     * @param contentArea the StackPane where the FXML screen will be loaded
     * @param fxmlPath the path to the FXML file
     */
    public void loadScreen(StackPane contentArea, String fxmlPath) {
        LOGGER.info("Attempting to load screen: {}", fxmlPath);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent screen = loader.load();
            contentArea.getChildren().setAll(screen);
            LOGGER.info("Screen loaded successfully: {}", fxmlPath);
        } catch (IOException e) {
            LOGGER.error("Error loading screen: {}", fxmlPath, e);
            contentArea.getChildren().setAll(new Label("Error loading screen."));
        }
    }
}
