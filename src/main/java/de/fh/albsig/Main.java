package de.fh.albsig;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Erstelle ein UiFx-Objekt
        UiFx ui = new UiFx(primaryStage);

        // Richte die Benutzeroberfläche ein
        ui.setupUI();

        // Zeige die Benutzeroberfläche an
        ui.show();
    }

    public static void main(String[] args) {
        logger.info("Application started.");
    }
}