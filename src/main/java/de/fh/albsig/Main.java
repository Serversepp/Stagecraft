package de.fh.albsig;

/**
 * The Main class serves as the entry point for the application.
 * It creates and displays the user interface (UI).
 *
 */

public class Main {
    /**
     * The main method is the entry point of the application.
     * It initializes the application by creating and displaying the user interface (UI).
     *
     * @param args command-line arguments passed to the program (not used in this application)
     */
    public static void main(String[] args) {

        // Create and show the UI
        Ui ui = new Ui();
        ui.show();
    }
}