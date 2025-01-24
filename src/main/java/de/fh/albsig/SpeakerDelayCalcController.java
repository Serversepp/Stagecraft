package de.fh.albsig;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The SpeakerDelayCalcController class controls the UI for speaker delay calculation.
 */
public class SpeakerDelayCalcController {

    public VBox delayCalc;

    /**
     * ComboBox to select the dimension (1D, 2D, 3D).
     */
    @FXML
    private ComboBox<String> dimensionComboBox;

    /**
     * Label for the X-field.
     */
    @FXML
    private Label dimensionLabelX;

    /**
     * TextField for the X distance.
     */
    @FXML
    private javafx.scene.control.TextField dimensionFieldX;

    /**
     * Label for the Y-field.
     */
    @FXML
    private Label dimensionLabelY;

    /**
     * TextField for the Y distance.
     */
    @FXML
    private javafx.scene.control.TextField dimensionFieldY;

    /**
     * Label for the Z-field.
     */
    @FXML
    private Label dimensionLabelZ;

    /**
     * TextField for the Z distance.
     */
    @FXML
    private javafx.scene.control.TextField dimensionFieldZ;

    /**
     * Label for displaying the calculation result.
     */
    @FXML
    private Label resultLabel;

    /**
     * Logger for log output.
     */
    private static final Logger logger = LogManager.getLogger(SpeakerDelayCalcController.class);

    /**
     * Initializes the controller.
     * Populates the ComboBox and adjusts the visibility of the fields.
     */
    @FXML
    public void initialize() {
        logger.info("Speaker Delay Calculator screen initialized.");

        dimensionComboBox.getItems().addAll("OneDimensional", "TwoDimensional", "ThreeDimensional");

        // By default, show only X, hide Y and Z
        dimensionLabelX.setVisible(true);
        dimensionFieldX.setVisible(true);

        dimensionLabelY.setVisible(false);
        dimensionFieldY.setVisible(false);

        dimensionLabelZ.setVisible(false);
        dimensionFieldZ.setVisible(false);
        dimensionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "OneDimensional":
                    dimensionLabelX.setVisible(true);
                    dimensionFieldX.setVisible(true);
                    dimensionLabelY.setVisible(false);
                    dimensionFieldY.setVisible(false);
                    dimensionLabelZ.setVisible(false);
                    dimensionFieldZ.setVisible(false);
                    break;
                case "TwoDimensional":
                    dimensionLabelX.setVisible(true);
                    dimensionFieldX.setVisible(true);
                    dimensionLabelY.setVisible(true);
                    dimensionFieldY.setVisible(true);
                    dimensionLabelZ.setVisible(false);
                    dimensionFieldZ.setVisible(false);
                    break;
                case "ThreeDimensional":
                    dimensionLabelX.setVisible(true);
                    dimensionFieldX.setVisible(true);
                    dimensionLabelY.setVisible(true);
                    dimensionFieldY.setVisible(true);
                    dimensionLabelZ.setVisible(true);
                    dimensionFieldZ.setVisible(true);
                    break;
                default:
                    logger.warn("Unknown dimension type selected: {}", newValue);
                    break;
            }
        });

    }

    /**
     * Invoked when the "Calculate Delay" button is clicked.
     * It calculates the speaker delay based on the provided distances.
     */
    @FXML
    public void calculateDelay() {
        logger.info("Calculate Speaker Delay button clicked.");

        String selectedDimension = dimensionComboBox.getValue();
        if (selectedDimension == null) {
            resultLabel.setText("Please select a dimension type first.");
            return;
        }
        Double x = parseDoubleSafe(dimensionFieldX.getText(), "X");
        if (x == null) {
            return;
        }


        Double y = 0.0;
        if (dimensionLabelY.isVisible()) {
            y = parseDoubleSafe(dimensionFieldY.getText(), "Y");
            if (y == null) {
                return;
            }
        }

        Double z = 0.0;
        if (dimensionLabelZ.isVisible()) {
            z = parseDoubleSafe(dimensionFieldZ.getText(), "Z");
            if (z == null) {
                return;
            }
        }
        // Calculate distance in cm depending on the chosen dimension
        double distanceInCm;
        switch (selectedDimension) {
            case "OneDimensional":
                distanceInCm = x;
                break;
            case "TwoDimensional":
                // Pythagorean theorem in 2D
                distanceInCm = Math.sqrt(x * x + y * y);
                break;
            case "ThreeDimensional":
                // Pythagorean theorem in 3D
                distanceInCm = Math.sqrt(x * x + y * y + z * z);
                break;
            default:
                // Should never happen
                resultLabel.setText("Unknown dimension type.");
                return;
        }
        // Convert distance from centimeters to meters
        double distanceInMeters = distanceInCm / 100.0;

        // Approximate speed of sound in air at 20°C (m/s)
        double speedOfSound = 343.0;

        // Time = distance / speed
        double timeInSeconds = distanceInMeters / speedOfSound;

        // Convert time to milliseconds
        double timeInMilliseconds = timeInSeconds * 1000;

        // Display the result
        resultLabel.setText(String.format("Delay: %.2f ms", timeInMilliseconds));
    }

    /**
     * Helper method to safely parse a string as a double.
     * Returns 0.0 if the string is empty or has an invalid format.
     *
     * @param value the string to parse
     * @return the parsed double or 0.0 if parsing fails
     */
    private Double parseDoubleSafe(String value, String fieldName) {
        try {
            String normalizedValue = value.replace(',', '.');
            return Double.parseDouble(normalizedValue);
        } catch (NumberFormatException e) {
            logger.warn("Invalid input for {}: '{}'", fieldName, value);
            resultLabel.setText(String.format(
                    "Please enter a valid number for field '%s' (input was: '%s').", fieldName, value
            ));
            return null;
        }
    }
}

