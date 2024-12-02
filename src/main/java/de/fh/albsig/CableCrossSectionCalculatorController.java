package de.fh.albsig;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller for the Cable Cross-Section Calculator screen.
 */
public class CableCrossSectionCalculatorController {

    private static final Logger logger = LogManager.getLogger(CableCrossSectionCalculatorController.class);

    @FXML
    private TextField lengthField;

    @FXML
    private ComboBox<String> phaseTypeComboBox;

    @FXML
    private ComboBox<String> currentTypeComboBox;

    @FXML
    private ComboBox<String> voltageComboBox;

    @FXML
    private TextField customVoltageField;

    @FXML
    private ComboBox<String> inputMethodComboBox;

    @FXML
    private TextField inputField;

    @FXML
    private Label resultField;

    @FXML
    protected void initialize() {
        // Populate combo boxes
        phaseTypeComboBox.getItems().addAll("Single-phase", "Three-phase");
        currentTypeComboBox.getItems().addAll("AC", "DC");
        voltageComboBox.getItems().addAll("230V", "400V", "5V", "12V", "20V", "24V", "48V", "Custom");
        inputMethodComboBox.getItems().addAll("Amperes", "Wattage");

        // Listener to show/hide custom voltage field
        voltageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            customVoltageField.setVisible("Custom".equals(newValue));
        });

        // Listener to update the input field prompt
        inputMethodComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("Amperes".equals(newValue)) {
                inputField.setPromptText("Enter current (A)");
            } else if ("Wattage".equals(newValue)) {
                inputField.setPromptText("Enter wattage (W)");
            }
        });

        logger.info("Cable Cross-Section Calculator initialized.");
    }

    @FXML
    private void calculateCrossSection() {
        try {
            double length = Double.parseDouble(lengthField.getText());
            String phaseType = phaseTypeComboBox.getValue();
            String currentType = currentTypeComboBox.getValue();
            String voltageSelection = voltageComboBox.getValue();
            double voltage = "Custom".equals(voltageSelection) ?
                    Double.parseDouble(customVoltageField.getText()) :
                    parseStandardVoltage(voltageSelection);

            // Determine the input method and calculate wattage
            String inputMethod = inputMethodComboBox.getValue();
            double wattage;

            if ("Amperes".equals(inputMethod)) {
                double current = Double.parseDouble(inputField.getText());
                wattage = current * voltage; // Calculate wattage from current and voltage
            } else if ("Wattage".equals(inputMethod)) {
                wattage = Double.parseDouble(inputField.getText());
            } else {
                throw new IllegalArgumentException("Invalid input method.");
            }

            // Calculate the cross-section
            double crossSection = computeCrossSection(length, phaseType, currentType, voltage, wattage);

            // Display the result
            resultField.setText(String.format("Result: %.2f mm²", crossSection));
            logger.info("Calculation successful. Result: {} mm²", crossSection);
        } catch (Exception e) {
            logger.error("Error during calculation: ", e);
            showAlert("Error", "Invalid input. Please check your entries and try again.");
        }
    }

    private double parseStandardVoltage(String voltageSelection) {
        switch (voltageSelection) {
            case "230V": return 230;
            case "400V": return 400;
            case "5V": return 5;
            case "12V": return 12;
            case "20V": return 20;
            case "24V": return 24;
            case "48V": return 48;
            default: throw new IllegalArgumentException("Invalid voltage selection");
        }
    }

    private double computeCrossSection(double length, String phaseType, String currentType, double voltage, double wattage) {
        double resistanceFactor = "Three-phase".equals(phaseType) ? Math.sqrt(3) : 1.0;
        double efficiencyFactor = "AC".equals(currentType) ? 1.5 : 1.0;

        // Calculate the base cross-section
        double baseCrossSection = (length * wattage) / (resistanceFactor * voltage * efficiencyFactor);

        // Enforce minimum cross-section
        return Math.max(baseCrossSection, 1.5);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
