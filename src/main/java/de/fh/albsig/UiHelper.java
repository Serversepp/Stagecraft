package de.fh.albsig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UiHelper {
    private JFrame frame;
    private JPanel panel;

    public UiHelper(String title, int width, int height) {
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel, BorderLayout.CENTER);
    }

    public void addLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
    }

    public JTextField addTextField(String placeholder, int columns) {
        JTextField textField = new JTextField(placeholder, columns);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));
        panel.add(textField);
        return textField;
    }

    public void addButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(actionListener);
        panel.add(button);
    }

    public void show() {
        frame.setVisible(true);
    }
}
