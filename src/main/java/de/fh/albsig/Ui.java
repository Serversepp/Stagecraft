package de.fh.albsig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Represents the main UI class for the application.
 */
public class Ui {
    private UiHelper uiHelper;

    /**
     * Constructs the UI and sets up its components.
     */
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
        // Define dropdown options
        String[] options = {"Option 1", "Option 2", "Option 3"};
        // add dropdown
        uiHelper.addDropdownMenu(options, selectedOption -> {
            System.out.println("Selected: " + selectedOption);
            // Additional logic for handling the selected option can go here
        });
    }

    /**
     * Displays the UI by delegating to the UiHelper.
     */
    public void show() {
        uiHelper.show();
    }
}
