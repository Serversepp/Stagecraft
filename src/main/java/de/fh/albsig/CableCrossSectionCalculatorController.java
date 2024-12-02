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
    private ComboBox<String> materialComboBox;

    @FXML
    private ComboBox<String> installationTypeComboBox;

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
        // Populate combo boxes
        materialComboBox.getItems().addAll("Copper", "Aluminum");
        installationTypeComboBox.getItems().addAll("Wall", "Conduit", "Underground");
        systemTypeComboBox.getItems().addAll("AC Single-phase", "AC Three-phase", "DC");
        inputMethodComboBox.getItems().addAll("Amperes", "Wattage");

        // Add listener to update voltage options based on system type
        systemTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateVoltageOptions(newValue);
        });

        // Add listener to show/hide custom voltage field
        voltageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            customVoltageField.setVisible("Custom".equals(newValue));
        });

        // Bind the button's disable property to incomplete inputs
        BooleanBinding allInputsProvided = Bindings.createBooleanBinding(
                () -> isInputIncomplete(),
                lengthField.textProperty(),
                materialComboBox.valueProperty(),
                installationTypeComboBox.valueProperty(),
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
        boolean isLengthEmpty = lengthField.getText().isEmpty();
        boolean isMaterialMissing = materialComboBox.getValue() == null;
        boolean isInstallationTypeMissing = installationTypeComboBox.getValue() == null;
        boolean isSystemTypeMissing = systemTypeComboBox.getValue() == null;
        boolean isVoltageMissing = voltageComboBox.getValue() == null || ("Custom".equals(voltageComboBox.getValue()) && customVoltageField.getText().isEmpty());
        boolean isInputMethodMissing = inputMethodComboBox.getValue() == null || inputField.getText().isEmpty();

        return isLengthEmpty || isMaterialMissing || isInstallationTypeMissing || isSystemTypeMissing || isVoltageMissing || isInputMethodMissing;
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
            String material = materialComboBox.getValue();
            String installationType = installationTypeComboBox.getValue();
            String systemType = systemTypeComboBox.getValue();
            String voltageSelection = voltageComboBox.getValue();
            double voltage = "Custom".equals(voltageSelection) ?
                    Double.parseDouble(customVoltageField.getText()) :
                    parseStandardVoltage(voltageSelection);

            String inputMethod = inputMethodComboBox.getValue();
            double wattage;
            double current;

            if ("Amperes".equals(inputMethod)) {
                current = Double.parseDouble(inputField.getText());
                wattage = current * voltage;
            } else {
                wattage = Double.parseDouble(inputField.getText());
                current = wattage / voltage;
            }

            double crossSection = computeCrossSection(length, material, installationType, systemType, voltage, wattage);
            double powerLoss = computePowerLoss(length, material, crossSection, current);

            String standardWiring = getRecommendedStandardWiring(crossSection);

            resultField.setText(String.format("Loss: %.2fW, Cross-section: %.2f mm², Standard Wiring: %s", powerLoss, crossSection, standardWiring));
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

    private double computeCrossSection(double length, String material, String installationType, String systemType, double voltage, double wattage) {
        double resistanceFactor = systemType.contains("Three-phase") ? Math.sqrt(3) : 1.0;
        double materialResistivity = "Copper".equals(material) ? 0.017 : 0.028; // Ohm mm²/m
        return (length * wattage) / (resistanceFactor * voltage * materialResistivity);
    }

    private double computePowerLoss(double length, String material, double crossSection, double current) {
        double resistivity = "Copper".equals(material) ? 0.017 : 0.028; // Ohm mm²/m
        return (2 * resistivity * length * current * current) / crossSection;
    }


    private String getRecommendedStandardWiring(double crossSection) {
        if (crossSection <= 1.5) return "1.5 mm²";
        if (crossSection <= 2.5) return "2.5 mm²";
        if (crossSection <= 4.0) return "4.0 mm²";
        if (crossSection <= 6.0) return "6.0 mm²";
        return "Greater than 6.0 mm² (consult a professional)";
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
