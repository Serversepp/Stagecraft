package de.fh.albsig;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    private ComboBox<String> systemTypeComboBox;

    @FXML
    private ComboBox<String> voltageComboBox;

    @FXML
    private TextField customVoltageField;

    @FXML
    private ComboBox<String> inputMethodComboBox;

    @FXML
    private TextField inputField;

    @FXML
    private Button calculateButton;

    @FXML
    private Label resultField;

    @FXML
    protected void initialize() {
        // Populate system type dropdown
        systemTypeComboBox.getItems().addAll("AC Single-phase", "AC Three-phase", "DC");

        // Populate input method dropdown
        inputMethodComboBox.getItems().addAll("Amperes", "Wattage");

        // Add listener to update voltage options based on system type
        systemTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateVoltageOptions(newValue);
        });

        // Add listener to show/hide custom voltage field
        voltageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            customVoltageField.setVisible("Custom".equals(newValue));
        });

        // Add listener to update the input field prompt
        inputMethodComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("Amperes".equals(newValue)) {
                inputField.setPromptText("Enter current (A)");
            } else if ("Wattage".equals(newValue)) {
                inputField.setPromptText("Enter wattage (W)");
            }
        });

        // Bind the button's disable property to incomplete inputs
        BooleanBinding allInputsProvided = Bindings.createBooleanBinding(
                () -> isInputIncomplete(),
                lengthField.textProperty(),
                systemTypeComboBox.valueProperty(),
                voltageComboBox.valueProperty(),
                customVoltageField.textProperty(),
                inputMethodComboBox.valueProperty(),
                inputField.textProperty()
        );
        calculateButton.disableProperty().bind(allInputsProvided);

        logger.info("Cable Cross-Section Calculator initialized.");
    }

    private boolean isInputIncomplete() {
        // Check if required inputs are empty or missing
        boolean isLengthEmpty = lengthField.getText().isEmpty();
        boolean isSystemTypeMissing = systemTypeComboBox.getValue() == null;
        boolean isVoltageMissing = voltageComboBox.getValue() == null || ("Custom".equals(voltageComboBox.getValue()) && customVoltageField.getText().isEmpty());
        boolean isInputMethodMissing = inputMethodComboBox.getValue() == null || inputField.getText().isEmpty();

        return isLengthEmpty || isSystemTypeMissing || isVoltageMissing || isInputMethodMissing;
    }

    private void updateVoltageOptions(String systemType) {
        voltageComboBox.getItems().clear();
        switch (systemType) {
            case "AC Single-phase":
                voltageComboBox.getItems().addAll("110V", "220V", "230V", "Custom");
                break;
            case "AC Three-phase":
                voltageComboBox.getItems().addAll("380V", "400V", "415V", "Custom");
                break;
            case "DC":
                voltageComboBox.getItems().addAll("12V", "24V", "48V", "Custom");
                break;
            default:
                logger.warn("Unknown system type selected: " + systemType);
        }
    }

    @FXML
    private void calculateCrossSection() {
        try {
            double length = Double.parseDouble(lengthField.getText());
            String systemType = systemTypeComboBox.getValue();
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
            double crossSection = computeCrossSection(length, systemType, voltage, wattage);

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
            case "110V": return 110;
            case "220V": return 220;
            case "230V": return 230;
            case "380V": return 380;
            case "400V": return 400;
            case "415V": return 415;
            case "12V": return 12;
            case "24V": return 24;
            case "48V": return 48;
            default: throw new IllegalArgumentException("Invalid voltage selection");
        }
    }

    private double computeCrossSection(double length, String systemType, double voltage, double wattage) {
        double resistanceFactor = systemType.contains("Three-phase") ? Math.sqrt(3) : 1.0;
        double efficiencyFactor = systemType.startsWith("AC") ? 1.5 : 1.0;

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
