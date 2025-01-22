package de.fh.albsig;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import org.testfx.api.FxToolkit;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import org.junit.jupiter.api.Tag;
import static org.mockito.Mockito.*;

@Tag("ui")
public class MainMenuTest extends ApplicationTest {

    private MainMenu mainMenu;
    private UiFx mockUiFx;
    private Stage stage;
    private static Throwable asyncException = null;

    @BeforeAll
    public static void setupGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            if (throwable instanceof IllegalStateException || throwable instanceof RuntimeException) {
                asyncException = throwable; // Capture relevant exceptions
            }
        });
    }

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
    public void setUp() throws Exception {
        asyncException = null; // Reset async exception tracker
        FxToolkit.setupFixture(() -> {
            stage.toFront();
            stage.requestFocus();
        });
        WaitForAsyncUtils.waitForFxEvents(); // Ensure pending events are completed
        reset(mockUiFx); // Reset mock state
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (asyncException != null) {
            Throwable exceptionToThrow = asyncException; // Preserve the exception for clear failure
            asyncException = null; // Reset immediately to prevent carryover
            Assertions.fail("Asynchronous exception occurred: " + exceptionToThrow.getMessage(), exceptionToThrow);
        }
        asyncException = null; // Ensure it's reset again to prevent carryover
        FxToolkit.cleanupStages(); // Cleanup JavaFX state
    }

    /**
     * Generates dynamic tests for all buttons in the button container.
     * Big Issues with timing and Focus
     * BUG Sometimes all test fail because the Stage gets out of focus even tho it is coded to force the focus
     * Needs some rework regarding the focussing of the Window
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
                            assertTrue(stage.isFocused(), "Stage is not focused");

                            // Capture the state before the click
                            String beforeState = captureUiState(contentArea);
                            WaitForAsyncUtils.waitForFxEvents();

                            // Simulate a button click

                            clickOn(button);
                            WaitForAsyncUtils.waitForFxEvents(); // Wait for the async thread to complete
                            //TODO Add the async Exeption handler

                            WaitForAsyncUtils.waitForFxEvents();

                            // Assert stage is focused
                            assertTrue(stage.isFocused(), "Stage is not focused");

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
