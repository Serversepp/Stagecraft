package de.fh.albsig;

import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.function.Consumer;

public class UiFx {

    private static final Logger logger = LogManager.getLogger(UiFx.class);

    public void addLabel(VBox layout, String text) {
        logger.info("Adding label with text: {}", text);
        Label label = new Label(text);
        layout.getChildren().add(label);
    }

    public void addButton(VBox layout, String text, Runnable onClick) {
        logger.info("Adding button with text: {}", text);
        Button button = new Button(text);
        button.setOnAction(event -> {
            logger.debug("Button clicked: {}", text);
            onClick.run();
        });
        layout.getChildren().add(button);
    }

    public void showAlert(String message) {
        logger.warn("Showing alert: {}", message);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void loadScreen(StackPane contentArea, String fxmlPath) {
        logger.info("Attempting to load screen: {}", fxmlPath);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent screen = loader.load();
            contentArea.getChildren().setAll(screen);
            logger.info("Screen loaded successfully: {}", fxmlPath);
        } catch (IOException e) {
            logger.error("Error loading screen: {}", fxmlPath, e);
            contentArea.getChildren().setAll(new Label("Error loading screen."));
        }
    }
}
