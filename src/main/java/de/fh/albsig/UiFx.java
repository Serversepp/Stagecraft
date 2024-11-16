package de.fh.albsig;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.function.Consumer;

public class UiFx {
    private VBox layout;
    private Stage stage;

    public UiFx(Stage stage) {
        this.stage = stage;
        layout = new VBox(10); // 10px spacing between components
        Scene scene = new Scene(layout, 400, 300);

        stage.setTitle("UiFx Example");
        stage.setScene(scene);
    }

    public void setupUI() {
        addLabel("Welcome to JavaFX!");
        TextField nameField = addTextField("Enter your name");
        addButton("Submit", () -> {
            String name = nameField.getText();
            showAlert("Hello, " + name + "!");
        });
        addDropdownMenu(new String[]{"Option 1", "Option 2", "Option 3"}, selected -> {
            showAlert("You selected: " + selected);
        });
    }

    public void addLabel(String text) {
        Label label = new Label(text);
        layout.getChildren().add(label);
    }

    public TextField addTextField(String placeholder) {
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        layout.getChildren().add(textField);
        return textField;
    }

    public void addButton(String text, Runnable onClick) {
        Button button = new Button(text);
        button.setOnAction(event -> onClick.run());
        layout.getChildren().add(button);
    }

    public void addDropdownMenu(String[] options, Consumer<String> onSelection) {
        ComboBox<String> dropdown = new ComboBox<>();
        dropdown.getItems().addAll(options);
        dropdown.setOnAction(event -> {
            String selected = dropdown.getValue();
            onSelection.accept(selected);
        });
        layout.getChildren().add(dropdown);
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        stage.show();
    }
}
