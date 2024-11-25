package de.fh.albsig;

import java.io.IOException;
import java.util.function.Consumer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * A JavaFX UI class for setting up and displaying a simple user interface.
 */
public class UiFx {

    private final VBox layout;
    private final Stage stage;

    /**
     * Constructor to initialize the UI.
     *
     * @param stage the primary stage for the application
     */
    public UiFx(Stage stage) {
        this.stage = stage;
        this.layout = new VBox(10); // 10px spacing between components
        Scene scene = new Scene(layout, 400, 300);

        stage.setTitle("UiFx Example");
        stage.setScene(scene);
    }

    /**
     * Sets up the UI components.
     */
    public void setupUi() {
        addLabel("Welcome to JavaFX!");

        TextField nameField = addTextField("Enter your name");

        addButton("Submit", () -> {
            String name = nameField.getText();
            showAlert("Hello, " + name + "!");
        });

        addDropdownMenu(new String[] {"Option 1", "Option 2", "Option 3"}, selected -> {
            showAlert("You selected: " + selected);
        });
    }

    /**
     * Adds a label to the UI.
     *
     * @param text the text to display on the label
     */
    public void addLabel(String text) {
        Label label = new Label(text);
        layout.getChildren().add(label);
    }

    /**
     * Adds a text field to the UI with a placeholder.
     *
     * @param placeholder the placeholder text for the text field
     * @return the created TextField object
     */
    public TextField addTextField(String placeholder) {
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        layout.getChildren().add(textField);
        return textField;
    }

    /**
     * Adds a button to the UI with a click action.
     *
     * @param text    the button text
     * @param onClick the action to perform when the button is clicked
     */
    public void addButton(String text, Runnable onClick) {
        Button button = new Button(text);
        button.setOnAction(event -> onClick.run());
        layout.getChildren().add(button);
    }

    /**
     * Adds a dropdown menu to the UI.
     *
     * @param options      the options for the dropdown
     * @param onSelection  the action to perform when an option is selected
     */
    public void addDropdownMenu(String[] options, Consumer<String> onSelection) {
        ComboBox<String> dropdown = new ComboBox<>();
        dropdown.getItems().addAll(options);
        dropdown.setOnAction(event -> {
            String selected = dropdown.getValue();
            onSelection.accept(selected);
        });
        layout.getChildren().add(dropdown);
    }

    /**
     * Shows an alert dialog with a given message.
     *
     * @param message the message to display in the alert
     */
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadScreen(StackPane contentArea, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent screen = loader.load();
            contentArea.getChildren().setAll(screen);
        } catch (IOException e) {
            e.printStackTrace();
            contentArea.getChildren().setAll(new Label("Error loading screen."));
        }
    }

    /**
     * Displays the stage.
     */
    public void show() {
        stage.show();
    }
}
