package de.fh.albsig;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class responsible for creating and displaying the main menu.
 */
public class MainMenu {

    private static final Logger logger = LogManager.getLogger(MainMenu.class);

    private final Stage stage;
    private final UiFx uiFx;

    public MainMenu(Stage stage) {
        this.stage = stage;
        this.uiFx = new UiFx();
    }

    /**
     * Sets up and displays the main menu.
     */
    public void setupAndShow() {
        logger.info("Setting up the main menu.");
        VBox layout = new VBox(10); // 10px spacing
        Scene scene = new Scene(layout, 800, 600);

        uiFx.addLabel(layout, "Welcome to the Main Menu!");

        MenuBar menuBar = new MenuBar();
        Menu navigationMenu = new Menu("Navigation");
        MenuItem screen1 = new MenuItem("Screen 1");
        MenuItem screen2 = new MenuItem("Screen 2");
        MenuItem screen3 = new MenuItem("Screen 3");

        navigationMenu.getItems().addAll(screen1, screen2, screen3);
        menuBar.getMenus().add(navigationMenu);

        StackPane contentArea = new StackPane();
        uiFx.addLabel(new VBox(contentArea), "Select a screen from the menu.");

        screen1.setOnAction(e -> uiFx.loadScreen(contentArea, "/screens/ohmCalc.fxml"));
        screen2.setOnAction(e -> uiFx.loadScreen(contentArea, "/screens/Screen2.fxml"));
        screen3.setOnAction(e -> uiFx.loadScreen(contentArea, "/screens/Screen3.fxml"));

        layout.getChildren().addAll(menuBar, contentArea);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();

        logger.info("Main menu displayed.");
    }
}
