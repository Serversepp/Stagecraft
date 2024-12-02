package de.fh.albsig;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.*;

public class MainMenuTest extends ApplicationTest {

    private MainMenu mainMenu;
    private UiFx mockUiFx;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        // Create a real instance of UiFx
        UiFx realUiFx = new UiFx();

        // Wrap the real instance with a spy
        mockUiFx = Mockito.spy(realUiFx);

        // Initialize MainMenu with the spy
        mainMenu = new MainMenu(stage, mockUiFx);

        // Set up and display the main menu
        mainMenu.setupAndShow();

        // Ensure the stage stays in the foreground
        Platform.runLater(() -> {
            stage.toFront();  // Bring the stage to the front
            stage.requestFocus();  // Ensure the stage is focused
        });
    }

    @BeforeEach
    public void setUp() {
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Generates dynamic tests for all buttons in the button container.
     *
     * @return A collection of dynamic tests for each button
     */
    @TestFactory
    public Collection<DynamicTest> dynamicButtonTests() {
        // Locate the button container
        VBox buttonContainer = (VBox) lookup("#buttonContainer").query();

        // Ensure the container exists
        assertNotNull(buttonContainer, "Button container was not found!");

        // Retrieve all buttons in the container
        return buttonContainer.getChildren().stream()
                .filter(node -> node instanceof Button) // Filter only buttons
                .map(node -> (Button) node) // Cast nodes to Buttons
                .map(button -> dynamicTest(
                        "Test for button: " + button.getText(), // Dynamic test name
                        () -> {
// Get content area
                            StackPane contentArea = lookup("#contentArea").query();
                            assertNotNull(contentArea, "Content area not found!");

                            // Assert stage is focused
                            assertTrue("Stage is not focused", stage.isFocused());

                            // Capture the state before the click
                            String beforeState = captureUiState(contentArea);
                            WaitForAsyncUtils.waitForFxEvents();

                            // Simulate a button click
                            // Assert that clicking the button throws a RuntimeException
                            try {
                                clickOn(button);
                                WaitForAsyncUtils.waitForFxEvents(); // Wait for the async thread to complete

                            } catch (Exception e) {
                                Assertions.fail("An exception was thrown during the test: " + e.getMessage(), e);
                            }


                            WaitForAsyncUtils.waitForFxEvents();

                            // Assert stage is focused
                            assertTrue("Stage is not focused", stage.isFocused());

                            // Capture the state after the click
                            String afterState = captureUiState(contentArea);

                            // Ensure the state has changed
                            assertNotEquals(beforeState, afterState, "UI state did not change!");


                            // Verify that loadScreen was called with the correct arguments
                            verify(mockUiFx, times(1))
                                    .loadScreen(eq(contentArea), anyString());

                            // Reset the spy for the next button
                            reset(mockUiFx);
                        }))
                .collect(Collectors.toList());
    }
    /**
     * Captures the UI state as a string representation.
     * This can be extended to capture specific properties of interest.
     *
     * @param node The node whose state needs to be captured.
     * @return A string representing the UI state.
     */
    private String captureUiState(Node node) {
        // Example: Capture the list of children and their IDs
        if (node instanceof StackPane) {
            StackPane stackPane = (StackPane) node;

            // Capture meaningful state details of all children
            return stackPane.getChildren().stream()
                    .map(child -> "ID: " + child.getId() + ", Class: " + child.getClass().getSimpleName())
                    .collect(Collectors.joining("; "));
        }

        // If it's not a StackPane, just return its toString()
        return node.toString();
    }
}
