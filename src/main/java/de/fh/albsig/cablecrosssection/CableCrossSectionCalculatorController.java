package de.fh.albsig.cablecrosssection;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CableCrossSectionCalculatorController {

    private static final Logger logger = LogManager.getLogger(CableCrossSectionCalculatorController.class);

    @FXML
    private TextField lengthField;

    @FXML
    private RadioButton copperRadioButton;

    @FXML
    private RadioButton aluminumRadioButton;

    private ToggleGroup materialToggleGroup;

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

    private final CableCrossSectionCalculatorLogic logic = new CableCrossSectionCalculatorLogic();

    @FXML
    protected void initialize() {
        // Initialize the ToggleGroup for material selection
        materialToggleGroup = new ToggleGroup();
        copperRadioButton.setToggleGroup(materialToggleGroup);
        aluminumRadioButton.setToggleGroup(materialToggleGroup);

        // Populate combo boxes
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
        boolean isInstallationTypeMissing = installationTypeComboBox.getValue() == null;
        boolean isSystemTypeMissing = systemTypeComboBox.getValue() == null;
        boolean isVoltageMissing = voltageComboBox.getValue() == null || ("Custom".equals(voltageComboBox.getValue()) && customVoltageField.getText().isEmpty());
        boolean isInputMethodMissing = inputMethodComboBox.getValue() == null || inputField.getText().isEmpty();

        return isLengthEmpty || isInstallationTypeMissing || isSystemTypeMissing || isVoltageMissing || isInputMethodMissing;
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
            String material = copperRadioButton.isSelected() ? "Copper" : "Aluminum";
            String installationType = installationTypeComboBox.getValue();
            String systemType = systemTypeComboBox.getValue();
            String voltageSelection = voltageComboBox.getValue();
            double voltage = "Custom".equals(voltageSelection) ?
                    Double.parseDouble(customVoltageField.getText()) :
                    logic.parseStandardVoltage(voltageSelection);

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

            // Use logic class for calculations
            double crossSection = logic.computeCrossSection(length, material, installationType, systemType, voltage, wattage);
            double powerLoss = logic.computePowerLoss(length, material, crossSection, current);
            String standardWiring = logic.getRecommendedStandardWiring(crossSection);

            // Display the results
            resultField.setText(String.format("Loss: %.2fW, Cross-section: %.2f mm², Standard Wiring: %s", powerLoss, crossSection, standardWiring));
        } catch (Exception e) {
            logger.error("Error during calculation: ", e);
            showAlert("Error", "Invalid input. Please check your entries and try again.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
