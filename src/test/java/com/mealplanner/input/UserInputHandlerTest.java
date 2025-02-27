package com.mealplanner.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserInputHandlerTest {
    private UserInputHandler userInputHandler;

    @BeforeEach
    void setUp() {
        userInputHandler = new UserInputHandler();
    }

    private void setInput(String input) {
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        userInputHandler = new UserInputHandler();
    }


    @Test
    void testReadLine_ReturnsCorrectInput() {
        setInput("test input\n");
        assertEquals("test input", userInputHandler.readLine());
    }

    @Test
    void testPromptCommand_ValidCommand() {
        setInput("add\n");
        assertEquals("add", userInputHandler.promptCommand());
    }

    @Test
    void testPromptCategory_ValidCategory() {
        setInput("breakfast\n");
        assertEquals("breakfast", userInputHandler.promptCategory());
    }

    @Test
    void testPromptCategoryShow_ValidCategory() {
        setInput("lunch\n");
        assertEquals("lunch", userInputHandler.promptCategoryShow());
    }

    @Test
    void testPromptName_ValidName() {
        setInput("Pasta\n");
        assertEquals("Pasta", userInputHandler.promptName());
    }

    @Test
    void testPromptIngredients_ValidIngredients() {
        setInput("tomato, cheese, basil\n");
        List<String> ingredients = userInputHandler.promptIngredients();
        assertEquals(List.of("tomato", "cheese", "basil"), ingredients);
    }
}
