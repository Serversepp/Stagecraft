package de.fh.albsig;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The SpeakerDelayCalcController class controls the UI for speaker delay calculation.
 */
public class SpeakerDelayCalcController {

    /**
     * Label for the dimension selection.
     */
    @FXML
    private Label dimensionlabel;

    /**
     * ComboBox to select the dimension (1D, 2D, 3D).
     */
    @FXML
    private ComboBox<String> dimensioncombobox;

    /**
     * Label for the X-field.
     */
    @FXML
    private Label xdimensionlabel;

    /**
     * TextField for the X distance.
     */
    @FXML
    private javafx.scene.control.TextField xdimensionfield;

    /**
     * Label for the Y-field.
     */
    @FXML
    private Label ydimensionlabel;

    /**
     * TextField for the Y distance.
     */
    @FXML
    private javafx.scene.control.TextField ydimensionfield;

    /**
     * Label for the Z-field.
     */
    @FXML
    private Label zdimensionlabel;

    /**
     * TextField for the Z distance.
     */
    @FXML
    private javafx.scene.control.TextField zdimensionfield;

    /**
     * Label for displaying the calculation result.
     */
    @FXML
    private Label resultlabel;

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

        dimensioncombobox.getItems().addAll("OneDimensional", "TwoDimensional", "ThreeDimensional");

        // By default, show only X, hide Y and Z
        xdimensionlabel.setVisible(true);
        xdimensionfield.setVisible(true);

        ydimensionlabel.setVisible(false);
        ydimensionfield.setVisible(false);

        zdimensionlabel.setVisible(false);
        zdimensionfield.setVisible(false);
        dimensioncombobox.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "OneDimensional":
                    xdimensionlabel.setVisible(true);
                    xdimensionfield.setVisible(true);
                    ydimensionlabel.setVisible(false);
                    ydimensionfield.setVisible(false);
                    zdimensionlabel.setVisible(false);
                    zdimensionfield.setVisible(false);
                    break;
                case "TwoDimensional":
                    xdimensionlabel.setVisible(true);
                    xdimensionfield.setVisible(true);
                    ydimensionlabel.setVisible(true);
                    ydimensionfield.setVisible(true);
                    zdimensionlabel.setVisible(false);
                    zdimensionfield.setVisible(false);
                    break;
                case "ThreeDimensional":
                    xdimensionlabel.setVisible(true);
                    xdimensionfield.setVisible(true);
                    ydimensionlabel.setVisible(true);
                    ydimensionfield.setVisible(true);
                    zdimensionlabel.setVisible(true);
                    zdimensionfield.setVisible(true);
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

        String selectedDimension = dimensioncombobox.getValue();
        if (selectedDimension == null) {
            resultlabel.setText("Please select a dimension type first.");
            return;
        }
        double x = parseDoubleSafe(xdimensionfield.getText());
        double y = parseDoubleSafe(ydimensionfield.getText());
        double z = parseDoubleSafe(zdimensionfield.getText());

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
                resultlabel.setText("Unknown dimension type.");
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
        resultlabel.setText(String.format("Delay: %.2f ms", timeInMilliseconds));
    }

    /**
     * Helper method to safely parse a string as a double.
     * Returns 0.0 if the string is empty or has an invalid format.
     *
     * @param value the string to parse
     * @return the parsed double or 0.0 if parsing fails
     */
    private double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logger.warn("Could not parse '" + value + "' as double, using 0.0");
            return 0.0;
        }
    }
}

