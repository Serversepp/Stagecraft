package de.fh.albsig;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Debugging: Zeige den Pfad zur FXML-Datei an
        System.out.println("FXML-URL: " + getClass().getResource("/de/fh/albsig/view/MainView.fxml"));

        // Lade die FXML-Datei
        Parent root = FXMLLoader.load(getClass().getResource("/de/fh/albsig/view/MainView.fxml"));

        // Setze die Szene und zeige das Fenster
        primaryStage.setTitle("JavaFX Minimal App");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
