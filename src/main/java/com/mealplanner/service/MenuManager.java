package com.mealplanner.service;

import com.mealplanner.database.DatabaseConnector;
import com.mealplanner.input.UserInputHandler;
import com.mealplanner.model.Meal;
import com.mealplanner.ui.MenuDisplay;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;

public class MenuManager {
    private UserInputHandler inputHandler;
    private DatabaseConnector dbConnector;

    public MenuManager() {
        this.inputHandler = new UserInputHandler();
        try {
            this.dbConnector = new DatabaseConnector();
            dbConnector.createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MenuManager(UserInputHandler inputHandler, DatabaseConnector dbConnector) {
        this.inputHandler = inputHandler;
        this.dbConnector = dbConnector;
    }

    public void run() throws SQLException {
        while (true) {
            String command = inputHandler.promptCommand();
            switch (command) {
                case "exit":
                    System.out.println("Bye!");
                    closeDatabaseConnection();
                    return;
                case "add":
                    handleAdd();
                    break;
                case "show":
                    handleShow();
                    break;
                case "plan":
                    handlePlan();
                    break;
                case "list plan":
                    handleListPlan();
                    break;
                case "save":
                    saveShoppingList();
                    break;
            }
        }
    }

    private void saveShoppingList() throws SQLException {
        if (!dbConnector.isPlanReady()) {
            System.out.println("Unable to save. Plan your meals first.");
            return;
        }

        System.out.println("Input a filename:");
        String fileName = inputHandler.readLine();

        try {
            Map<String, Integer> shoppingList = dbConnector.generateShoppingList();
            try (PrintWriter writer = new PrintWriter(fileName)) {
                for (Map.Entry<String, Integer> entry : shoppingList.entrySet()) {
                    writer.println(entry.getKey() + (entry.getValue() > 1 ? " x" + entry.getValue() : ""));
                }
            }
            System.out.println("Saved!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }


    private void handleAdd() {
        try {
            String category = inputHandler.promptCategory();
            String name = inputHandler.promptName();
            List<String> ingredients = inputHandler.promptIngredients();
            Meal meal = new Meal(name, category, ingredients);
            dbConnector.addMeal(meal);
            System.out.println("The meal has been added!");
        } catch (SQLException e) {
            System.out.println("Error adding meal to the database.");
            e.printStackTrace();
        }
    }

    private void handleShow() {
        try {
            String category = inputHandler.promptCategoryShow();
            List<Meal> meals = dbConnector.getMealsByCategory(category);
            if (meals.isEmpty()) {
                System.out.println("No meals found. ");
            } else {
                MenuDisplay.displayMeals(category, meals);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving meals from the database.");
            e.printStackTrace();
        }
    }

    private void handlePlan() {
        try {
            dbConnector.clearPlan();
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            for (String day : days) {
                System.out.println(day);
                for (String category : new String[]{"breakfast", "lunch", "dinner"}) {
                    List<Meal> meals = dbConnector.getMealsByCategory(category);
                    if (meals.isEmpty()) {
                        System.out.println("No meals found for " + category + ". Please add some meals first.");
                        return;
                    }
                    meals.sort((m1, m2) -> m1.getName().compareToIgnoreCase(m2.getName()));
                    for (Meal meal : meals) {
                        System.out.println(meal.getName());
                    }
                    while (true) {
                        System.out.println("Choose the " + category + " for " + day + " from the list above:");
                        String chosenName = inputHandler.readLine();
                        Meal chosenMeal = meals.stream()
                                .filter(m -> m.getName().equals(chosenName))
                                .findFirst()
                                .orElse(null);
                        if (chosenMeal != null) {
                            dbConnector.addPlanEntry(day, category, chosenMeal.getMealId());
                            break;
                        } else {
                            System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                        }
                    }
                }
                System.out.println("Yeah! We planned the meals for " + day + ".");
                System.out.println();
            }
            displayPlan(dbConnector.getPlan());
        } catch (SQLException e) {
            System.out.println("Error planning meals.");
            e.printStackTrace();
        }
    }

    private void handleListPlan() {
        try {
            List<DatabaseConnector.PlanEntry> plan = dbConnector.getPlan();
            if (plan.isEmpty()) {
                System.out.println("Database does not contain any meal plans");
            } else {
                displayPlan(plan);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving plan from the database.");
            e.printStackTrace();
        }
    }

    private void displayPlan(List<DatabaseConnector.PlanEntry> plan) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] categories = {"Breakfast", "Lunch", "Dinner"};
        for (String day : days) {
            System.out.println(day);
            for (String category : categories) {
                for (DatabaseConnector.PlanEntry entry : plan) {
                    if (entry.day.equals(day) && entry.category.equals(category.toLowerCase())) {
                        System.out.println(category + ": " + entry.mealName);
                        break;
                    }
                }
            }
            System.out.println();
        }
    }


    private void closeDatabaseConnection() {
        try {
            if (dbConnector != null) {
                dbConnector.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection.");
            e.printStackTrace();
        }
    }
}