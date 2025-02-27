package com.mealplanner.service;

import com.mealplanner.database.DatabaseConnector;
import com.mealplanner.input.UserInputHandler;
import com.mealplanner.model.Meal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuManagerTest {
    private MenuManager menuManager;
    private UserInputHandler inputHandler;
    private DatabaseConnector dbConnector;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() throws SQLException {
        inputHandler = mock(UserInputHandler.class);
        dbConnector = mock(DatabaseConnector.class);
        menuManager = new MenuManager(inputHandler, dbConnector);
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testRunExit() throws SQLException {
        when(inputHandler.promptCommand()).thenReturn("exit");
        menuManager.run();
        assertTrue(outputStream.toString().contains("Bye!"));
        verify(dbConnector).close();
    }

    @Test
    void testRunAddMeal() throws SQLException {
        when(inputHandler.promptCommand()).thenReturn("add", "exit");
        when(inputHandler.promptCategory()).thenReturn("breakfast");
        when(inputHandler.promptName()).thenReturn("Omelette");
        when(inputHandler.promptIngredients()).thenReturn(List.of("Eggs", "Salt"));

        menuManager.run();

        verify(dbConnector).addMeal(any(Meal.class));
        assertTrue(outputStream.toString().contains("The meal has been added!"));
    }

    @Test
    void testRunShowMeals() throws SQLException {
        when(inputHandler.promptCommand()).thenReturn("show", "exit");
        when(inputHandler.promptCategoryShow()).thenReturn("breakfast");
        when(dbConnector.getMealsByCategory("breakfast")).thenReturn(List.of(new Meal("Omelette", "breakfast", List.of("Eggs"))));

        menuManager.run();

        assertTrue(outputStream.toString().contains("Omelette"));
    }

    @Test
    void testRunShowNoMeals() throws SQLException {
        when(inputHandler.promptCommand()).thenReturn("show", "exit");
        when(inputHandler.promptCategoryShow()).thenReturn("breakfast");
        when(dbConnector.getMealsByCategory("breakfast")).thenReturn(Collections.emptyList());

        menuManager.run();

        assertTrue(outputStream.toString().contains("No meals found."));
    }

    @Test
    void testRunPlanMeals_NoMealsAvailable() throws SQLException {
        when(inputHandler.promptCommand()).thenReturn("plan", "exit");
        when(dbConnector.getMealsByCategory(anyString())).thenReturn(Collections.emptyList());

        menuManager.run();

        assertTrue(outputStream.toString().contains("No meals found"));
    }

    @Test
    void testRunListPlan_NoPlanExists() throws SQLException {
        when(inputHandler.promptCommand()).thenReturn("list plan", "exit");
        when(dbConnector.getPlan()).thenReturn(Collections.emptyList());

        menuManager.run();

        assertTrue(outputStream.toString().contains("Database does not contain any meal plans"));
    }
}
