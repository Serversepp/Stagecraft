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
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller for the Cable Cross-Section Calculator application.
 * This class manages user interactions, updates the UI, and delegates
 * complex computations to the CableCrossSectionCalculatorLogic class.
 */
public class CableCrossSectionCalculatorController {

    private static final Logger logger =
            LogManager.getLogger(CableCrossSectionCalculatorController.class);

    /**
     * Input field for cable length (in meters).
     */
    @FXML
    private TextField lengthField;

    /**
     * Radio button for selecting Copper as the cable material.
     */
    @FXML
    private RadioButton copperRadioButton;

    /**
     * Radio button for selecting Aluminum as the cable material.
     */
    @FXML
    private RadioButton aluminumRadioButton;

    /**
     * Toggle group to ensure only one material can be selected at a time.
     */
    private ToggleGroup materialToggleGroup;

    /**
     * Dropdown for selecting the installation type (e.g., Wall, Conduit).
     * This option is only visible for AC system types.
     */
    @FXML
    private ComboBox<String> installationTypeComboBox;

    /**
     * Container for the installation type dropdown.
     * This is dynamically shown or hidden based on the selected system type.
     */
    @FXML
    private VBox installationTypeContainer;

    /**
     * Dropdown for selecting the type of electrical system (e.g., AC Single-phase).
     */
    @FXML
    private ComboBox<String> systemTypeComboBox;

    /**
     * Dropdown for selecting standard or custom voltage values.
     */
    @FXML
    private ComboBox<String> voltageComboBox;

    /**
     * Input field for entering a custom voltage value.
     * This field is visible only when "Custom" is selected in the voltage dropdown.
     */
    @FXML
    private TextField customVoltageField;

    /**
     * Dropdown for selecting the input method (Amperes or Wattage).
     */
    @FXML
    private ComboBox<String> inputMethodComboBox;

    /**
     * Input field for entering the value corresponding to the selected input method.
     * For example, if Amperes is selected, the user enters the current.
     */
    @FXML
    private TextField inputField;

    /**
     * Button to trigger the cable cross-section calculation.
     */
    @FXML
    private Button calculateButton;

    /**
     * Label to display the result of the cable cross-section calculation,
     * including cross-section size, standard wiring recommendation, and power loss.
     */
    @FXML
    private Label resultField;

    /**
     * Logic class to perform calculations related to cable cross-sections and power loss.
     */
    private final CableCrossSectionCalculatorLogic logic = new CableCrossSectionCalculatorLogic();

    /**
     * Initializes the controller by setting up UI bindings, event listeners, and default values.
     */
    @FXML
    protected void initialize() {
        // Initialize responsive design
        setupResponsiveDesign();

        // Initialize the toggle group for material selection
        materialToggleGroup = new ToggleGroup();
        copperRadioButton.setToggleGroup(materialToggleGroup);
        aluminumRadioButton.setToggleGroup(materialToggleGroup);

        // Populate the dropdowns
        installationTypeComboBox.getItems().addAll("Wall", "Conduit", "Underground");
        systemTypeComboBox.getItems().addAll("AC Single-phase", "AC Three-phase", "DC");
        inputMethodComboBox.getItems().addAll("Amperes", "Wattage");

        // Event listener for system type
        systemTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateVoltageOptions(newValue);

            boolean isAlternatingCurrent =
                    "AC Single-phase".equals(newValue) || "AC Three-phase".equals(newValue);
            installationTypeContainer.setVisible(isAlternatingCurrent);
        });

        // Show custom voltage field when "Custom" is selected
        voltageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            customVoltageField.setVisible("Custom".equals(newValue));
        });

        // Restrict numeric inputs for length, custom voltage, and input value fields
        enforceNumericInput(lengthField);
        enforceNumericInput(customVoltageField);
        enforceNumericInput(inputField);

        // Disable calculate button if inputs are incomplete
        BooleanBinding allInputsProvided = Bindings.createBooleanBinding(
                this::isInputIncomplete,
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

    /**
     * Adjusts the scene size dynamically to fit the window's content.
     * This method ensures the layout adapts when components are shown/hidden.
     */
    private void setupResponsiveDesign() {
        installationTypeContainer.managedProperty()
                .bind(installationTypeContainer.visibleProperty());
        customVoltageField.managedProperty().bind(customVoltageField.visibleProperty());
    }

    /**
     * Enforces numeric-only input for a given TextField.
     *
     * @param textField The TextField to apply the restriction to.
     */
    private void enforceNumericInput(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("\\d*")) {
                return change; // Allow only numeric input
            }
            return null; // Reject non-numeric input
        }));
    }

    /**
     * Checks if any required inputs are incomplete.
     *
     * @return true if any required input is missing, false otherwise.
     */
    private boolean isInputIncomplete() {
        boolean isLengthEmpty = lengthField.getText().isEmpty();
        boolean isSystemTypeMissing = systemTypeComboBox.getValue() == null;
        boolean isVoltageMissing = 
                voltageComboBox.getValue() == null
                        || ("Custom".equals(voltageComboBox.getValue())
                        && customVoltageField.getText().isEmpty());
        boolean isInputMethodMissing =
                inputMethodComboBox.getValue() == null || inputField.getText().isEmpty();

        return isLengthEmpty || isSystemTypeMissing || isVoltageMissing || isInputMethodMissing;
    }

    /**
     * Updates the voltage dropdown options based on the selected system type.
     *
     * @param systemType The selected system type (e.g., AC Single-phase, DC).
     */
    private void updateVoltageOptions(String systemType) {
        voltageComboBox.getItems().clear(); // Clear any existing options

        if (systemType == null) {
            return;
        }

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

        if (!voltageComboBox.getItems().isEmpty()) {
            voltageComboBox.getSelectionModel().select(0); // Default to the first option
        }
    }

    /**
     * Handles the calculation of cable cross-section and power loss.
     * Reads user inputs, validates them, and uses the logic class to compute
     * the required cross-section, power loss, and recommended wiring.
     */
    @FXML
    private void calculateCrossSection() {
        try {
            // Parse user inputs
            double length = Double.parseDouble(lengthField.getText());
            String material = copperRadioButton.isSelected() ? "Copper" : "Aluminum";
            double conductivity = "Copper".equals(material) ? 56 : 37; // Conductivity for CO or AL
            double voltageDrop = 5.0; // Default voltage drop in volts
            String systemType = systemTypeComboBox.getValue();
            String voltageSelection = voltageComboBox.getValue();
            double voltage = "Custom".equals(voltageSelection)
                    ? Double.parseDouble(customVoltageField.getText())
                    : logic.parseStandardVoltage(voltageSelection);

            double cosPhi = 0.9; // Default power factor
            double crossSection;
            double powerLoss;
            double current; // Initialize current for power loss calculation

            // Compute cross-section based on system type
            if ("AC Three-phase".equals(systemType)) {
                double powerInKiloWatts  = Double.parseDouble(inputField.getText());
                current = (powerInKiloWatts * 1000) / (voltage * 1.732); // Current for three-phase
                crossSection = logic.computeThreePhaseCrossSection(
                        length, powerInKiloWatts, voltage, cosPhi, conductivity, voltageDrop);

            } else if ("AC Single-phase".equals(systemType)) {
                current = Double.parseDouble(inputField.getText());
                crossSection = logic.computeSinglePhaseCrossSection(
                        length, current, cosPhi, conductivity, voltageDrop);

            } else {
                throw new IllegalArgumentException("Invalid system type");
            }

            // Compute power loss
            powerLoss = logic.computePowerLoss(length, current, material, crossSection);

            // Recommend standard wiring size
            String standardWiring = logic.getRecommendedStandardWiring(crossSection);

            // Display results
            resultField.setText(
                    String.format(
                            "Cross-section: %.2f mm², Standard Wiring: %s, Power Loss: %.2f W",
                    crossSection, standardWiring, powerLoss));
        } catch (Exception e) {
            logger.error("Error during calculation: ", e);
            showAlert("Error", "Invalid input. Please check your entries and try again.");
        }
        logger.info("Cable Cross-Section Calculator calculated.");
    }

    /**
     * Displays an alert message for errors or invalid inputs.
     *
     * @param title   The title of the alert dialog.
     * @param message The message to display in the alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
        logger.info("Cable Cross-Section Alert showed.");
    }
}
