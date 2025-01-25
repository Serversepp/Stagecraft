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

        // Default settings: X visible, Y & Z hidden
        setFieldsVisibility(false, false);

        dimensionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "OneDimensional":
                    setFieldsVisibility(false, false);
                    break;
                case "TwoDimensional":
                    setFieldsVisibility(true, false);
                    break;
                case "ThreeDimensional":
                    setFieldsVisibility(true, true);
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
        double delayMs;
        switch (selectedDimension) {
            case "OneDimensional":
                delayMs = calculateOneDimensional(x);
                break;
            case "TwoDimensional":
                delayMs = calculateTwoDimensional(x, y);
                break;
            case "ThreeDimensional":
                delayMs = calculateThreeDimensional(x, y, z);
                break;
            default:
                resultLabel.setText("Unknown dimension type.");
                return;
        }

        // Display the result
        resultLabel.setText(String.format("Delay: %.2f ms", delayMs));
    }

    /**
     * Calculates the delay for a one-dimensional distance (x).
     *
     * @param xInCm the distance in centimeters
     * @return the resulting delay in milliseconds
     */
    public double calculateOneDimensional(double xInCm) {
        return distanceToDelay(xInCm);
    }

    /**
     * Calculates the delay for a two-dimensional distance (x, y).
     * Uses the Pythagorean theorem in 2D: distance = sqrt(x^2 + y^2).
     *
     * @param xinCm the X distance in centimeters
     * @param yinCm the Y distance in centimeters
     * @return the resulting delay in milliseconds
     */
    public double calculateTwoDimensional(double xinCm, double yinCm) {
        double distance = Math.sqrt(xinCm * xinCm + yinCm * yinCm);
        return distanceToDelay(distance);
    }

    /**
     * Calculates the delay for a three-dimensional distance (x, y, z).
     * Uses the Pythagorean theorem in 3D: distance = sqrt(x^2 + y^2 + z^2).
     *
     * @param xinCm the X distance in centimeters
     * @param yinCm the Y distance in centimeters
     * @param zinCm the Z distance in centimeters
     * @return the resulting delay in milliseconds
     */
    public double calculateThreeDimensional(double xinCm, double yinCm, double zinCm) {
        double distance = Math.sqrt(xinCm * xinCm + yinCm * yinCm + zinCm * zinCm);
        return distanceToDelay(distance);
    }

    /**
     * Helper method to convert a distance in centimeters to a delay in milliseconds.
     * Assumes a speed of sound of approximately 343 m/s.
     *
     * @param distanceInCm the distance in centimeters
     * @return the delay in milliseconds
     */
    private double distanceToDelay(double distanceInCm) {
        double speedOfSound = 343.0;  // speed of sound in m/s
        double distanceInMeters = distanceInCm / 100.0;
        double timeInSeconds = distanceInMeters / speedOfSound;
        return timeInSeconds * 1000.0;
    }

    /**
     * Helper to show/hide the X, Y, Z labels and text fields.
     */
    private void setFieldsVisibility(boolean showY, boolean showZ) {
        dimensionLabelX.setVisible(true);
        dimensionFieldX.setVisible(true);
        dimensionLabelY.setVisible(showY);
        dimensionFieldY.setVisible(showY);
        dimensionLabelZ.setVisible(showZ);
        dimensionFieldZ.setVisible(showZ);
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
                    "Please enter a valid number for field '%s' (input was: '%s').",
                    fieldName, value
            ));
            return null;
        }
    }
}

