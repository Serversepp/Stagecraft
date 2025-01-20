package de.fh.albsig;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class RefuelGeneratorController {

    @FXML
    private ComboBox<String> dropdown;

    @FXML
    private Button New_Button;

    @FXML
    private Button Edit_Button;

    @FXML
    private Slider slider_load;

    @FXML
    private Slider slider_fuel_level;

    @FXML
    private DatePicker begindatePicker;

    @FXML
    private ComboBox<String> beginhourComboBox;

    @FXML
    private ComboBox<String> beginminuteComboBox;

    @FXML
    private ComboBox<String> beginampmComboBox;

    @FXML
    private RadioButton dateRadioButton;

    @FXML
    private RadioButton durationRadioButton;

    @FXML
    private VBox dateTimeSelector;

    @FXML
    private VBox durationInput;

    @FXML
    private DatePicker enddatePicker;

    @FXML
    private ComboBox<String> hourComboBox;

    @FXML
    private ComboBox<String> minuteComboBox;

    @FXML
    private ComboBox<String> ampmComboBox;

    @FXML
    private TextField durationTextField;

    @FXML
    private Label resultLabel;

    private ToggleGroup toggleGroup;

    @FXML
    public void initialize() {
        // Initialize the ToggleGroup and associate it with the RadioButtons
        toggleGroup = new ToggleGroup();
        dateRadioButton.setToggleGroup(toggleGroup);
        durationRadioButton.setToggleGroup(toggleGroup);

        // Set default selected RadioButton
        toggleGroup.selectToggle(dateRadioButton);

        // Add listener to the ToggleGroup to switch between date-time and duration inputs
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == dateRadioButton) {
                dateTimeSelector.setVisible(true);
                dateTimeSelector.setManaged(true);
                durationInput.setVisible(false);
                durationInput.setManaged(false);
            } else if (newValue == durationRadioButton) {
                dateTimeSelector.setVisible(false);
                dateTimeSelector.setManaged(false);
                durationInput.setVisible(true);
                durationInput.setManaged(true);
            }
        });

        // Initialize time ComboBox values
        for (int i = 1; i <= 12; i++) {
            beginhourComboBox.getItems().add(String.format("%02d", i));
            hourComboBox.getItems().add(String.format("%02d", i));
        }
        for (int i = 0; i < 60; i++) {
            beginminuteComboBox.getItems().add(String.format("%02d", i));
            minuteComboBox.getItems().add(String.format("%02d", i));
        }
        beginampmComboBox.getItems().addAll("AM", "PM");
        ampmComboBox.getItems().addAll("AM", "PM");

        // Set default time values
        beginhourComboBox.setValue("12");
        beginminuteComboBox.setValue("00");
        beginampmComboBox.setValue("AM");
        hourComboBox.setValue("12");
        minuteComboBox.setValue("00");
        ampmComboBox.setValue("AM");
    }

    // Example method to get selected generator data
    @FXML
    private void handleNewButtonAction() {
        resultLabel.setText("New generator configuration started.");
    }

    @FXML
    private void handleEditButtonAction() {
        resultLabel.setText("Editing generator configuration.");
    }

    @FXML
    public String getResult() {
        if (dateRadioButton.isSelected()) {
            return "Start Date and Time: " + begindatePicker.getValue() + " " +
                    beginhourComboBox.getValue() + ":" + beginminuteComboBox.getValue() + " " +
                    beginampmComboBox.getValue() + "\n" +
                    "End Date and Time: " + enddatePicker.getValue() + " " +
                    hourComboBox.getValue() + ":" + minuteComboBox.getValue() + " " +
                    ampmComboBox.getValue();
        } else if (durationRadioButton.isSelected()) {
            return "Duration: " + durationTextField.getText() + " hours";
        }
        return "No selection made.";
    }
}
