package de.fh.albsig;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller for the Ohm Calculator screen.
 */
public class OhmCalcController {

    private static final Logger logger = LogManager.getLogger(OhmCalcController.class);

    @FXML
    private TextField voltageField;

    @FXML
    private TextField currentField;

    @FXML
    private Label resultLabel;

    /**
     * Initializes the screen. This method is automatically called after the FXML file is loaded.
     */
    @FXML
    public void initialize() {
        logger.info("Ohm Calculator screen initialized.");
    }

    /**
     * Handles the calculation of resistance based on the voltage and current inputs.
     */
    @FXML
    public void handleCalculateResistance() {
        logger.info("Calculate Resistance button clicked.");

        try {
            // Parse input values
            double voltage = Double.parseDouble(voltageField.getText());
            double current = Double.parseDouble(currentField.getText());

            // Validate input
            if (current == 0) {
                throw new ArithmeticException("Current cannot be zero.");
            }

            // Calculate resistance
            double resistance = voltage / current;
            resultLabel.setText(String.format("Result: %.2f Ω", resistance));
            logger.info("Calculated resistance: {} Ω", resistance);

        } catch (NumberFormatException e) {
            logger.error("Invalid input: non-numeric value entered.", e);
            showAlert("Input Error", "Please enter valid numeric values for voltage and current.");
        } catch (ArithmeticException e) {
            logger.error("Error in calculation: {}", e.getMessage(), e);
            showAlert("Calculation Error", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred during calculation.", e);
            showAlert("Error", "An unexpected error occurred. Please try again.");
        }
    }

    /**
     * Displays an informational alert dialog.
     *
     * @param title   the title of the alert dialog
     * @param message the message to display
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
