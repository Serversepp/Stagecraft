package de.fh.albsig;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * A helper class to simplify the creation of a Swing-based UI.
 */
public class UiHelper {
    private JFrame frame;
    private JPanel panel;

    /**
     * Constructs a new UI helper with a specified title, width, and height.
     *
     * @param title  the title of the window
     * @param width  the width of the window
     * @param height the height of the window
     */
    public UiHelper(String title, int width, int height) {
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel, BorderLayout.CENTER);
    }

    /**
     * Adds a label with the specified text to the UI.
     *
     * @param text the text to display in the label
     */
    public void addLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
    }

    /**
     * Adds a text field with a placeholder and specific number of columns to the UI.
     *
     * @param placeholder the placeholder text displayed in the text field
     * @param columns     the number of columns for the text field
     * @return the created JTextField
     */
    public JTextField addTextField(String placeholder, int columns) {
        JTextField textField = new JTextField(placeholder, columns);
        textField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));
        panel.add(textField);
        return textField;
    }

    /**
     * Adds a button with the specified text and action listener to the UI.
     *
     * @param text           the text displayed on the button
     * @param actionListener the action listener triggered when the button is clicked
     */
    public void addButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(actionListener);
        panel.add(button);
    }

    /**
     * Adds a dropdown menu (combo box) to the UI with the specified options and a callback for selection changes.
     *
     * <p>
     * Example usage:
     * </p>
     * <pre>{@code
     * // Create an instance of UiHelper
     * UiHelper uiHelper = new UiHelper("Dropdown Example", 400, 300);
     *
     * // Define dropdown options
     * String[] options = {"Option 1", "Option 2", "Option 3"};
     *
     * // Add a dropdown menu and define the callback for selection changes
     * uiHelper.addDropdownMenu(options, selectedOption -> {
     *     System.out.println("User selected: " + selectedOption);
     *     // Additional logic for the selected option
     * });
     *
     * // Display the UI
     * uiHelper.show();
     * }</pre>
     *
     * @param options          an array of string options to be displayed in the dropdown menu
     * @param onSelectionChange a callback function that accepts the selected option as a parameter
     * @return the created JComboBox object
     */
    public JComboBox<String> addDropdownMenu(String[] options, java.util.function.Consumer<String> onSelectionChange) {
        JComboBox<String> dropdown = new JComboBox<>(options);
        dropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, dropdown.getPreferredSize().height));

        // Add a listener to handle selection changes internally
        dropdown.addActionListener(e -> {
            String selectedOption = (String) dropdown.getSelectedItem();
            onSelectionChange.accept(selectedOption); // Notify the callback with the selected option
        });

        panel.add(dropdown);
        return dropdown;
    }
    /**
     * Displays the UI by making the frame visible.
     */
    public void show() {
        frame.setVisible(true);
    }
}
