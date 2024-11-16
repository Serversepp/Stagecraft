package de.fh.albsig;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class Ui {
    private UiHelper uiHelper;

    public Ui() {
        // Create UI using the helper
        uiHelper = new UiHelper("Basic UI App", 400, 300);

        // Add UI components
        uiHelper.addLabel("Welcome to Basic UI App");

        // Text field for user input
        JTextField textField = uiHelper.addTextField("Enter your name", 20);

        // Button with an action
        uiHelper.addButton("Click Me", (ActionEvent e) -> {
            String name = textField.getText();
            JOptionPane.showMessageDialog(null, "Hello, " + name + "!");
        });
    }

    public void show() {
        uiHelper.show();
    }
}
