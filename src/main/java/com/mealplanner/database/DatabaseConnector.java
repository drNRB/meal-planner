package com.mealplanner.database;

import com.mealplanner.model.Meal;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnector {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/meals_db";
    private static final String USER = "postgres";
    private static final String PASS = "1111";

    private Connection connection;

    public DatabaseConnector() throws SQLException {
        this.connection = DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public DatabaseConnector(Connection connection) {
        this.connection = connection;
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS meals (" +
                    "meal_id INTEGER PRIMARY KEY, " +
                    "category VARCHAR(255), " +
                    "meal VARCHAR(255))");


            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ingredients (" +
                    "ingredient_id INTEGER PRIMARY KEY, " +
                    "ingredient VARCHAR(255), " +
                    "meal_id INTEGER REFERENCES meals(meal_id))");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS plan (" +
                    "plan_id SERIAL PRIMARY KEY, " +
                    "day VARCHAR(10), " +
                    "category VARCHAR(10), " +
                    "meal_id INTEGER REFERENCES meals(meal_id))");
        }
    }

    public int getNextMealId() throws SQLException {
        String query = "SELECT COALESCE(MAX(meal_id), 0) + 1 FROM meals";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 1;
    }

    public int getNextIngredientId() throws SQLException {
        String query = "SELECT COALESCE(MAX(ingredient_id), 0) + 1 FROM ingredients";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 1;
    }

    public void addMeal(Meal meal) throws SQLException {
        int mealId = getNextMealId();
        String insertMealQuery = "INSERT INTO meals (meal_id, category, meal) VALUES (?, ?, ?)";
        try (PreparedStatement mealStmt = connection.prepareStatement(insertMealQuery)) {
            mealStmt.setInt(1, mealId);
            mealStmt.setString(2, meal.getCategory());
            mealStmt.setString(3, meal.getName());
            mealStmt.executeUpdate();
        }
        addIngredients(meal.getIngredients(), mealId);
    }

    private void addIngredients(List<String> ingredients, int mealId) throws SQLException {
        String insertIngredientQuery = "INSERT INTO ingredients (ingredient_id, ingredient, meal_id) VALUES (?, ?, ?)";
        for (String ingredient : ingredients) {
            int ingredientId = getNextIngredientId();
            try (PreparedStatement ingredientStmt = connection.prepareStatement(insertIngredientQuery)) {
                ingredientStmt.setInt(1, ingredientId);
                ingredientStmt.setString(2, ingredient);
                ingredientStmt.setInt(3, mealId);
                ingredientStmt.executeUpdate();
            }
        }
    }


    public List<Meal> getMealsByCategory(String category) throws SQLException {
        List<Meal> meals = new ArrayList<>();
        String query = "SELECT m.meal_id, m.meal, i.ingredient " +
                "FROM meals m " +
                "JOIN ingredients i ON m.meal_id = i.meal_id " +
                "WHERE m.category = ? " +
                "ORDER BY m.meal_id";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, category);
            try (ResultSet resultSet = statement.executeQuery()) {
                Meal currentMeal = null;
                while (resultSet.next()) {
                    int mealId = resultSet.getInt("meal_id");
                    String mealName = resultSet.getString("meal");
                    String ingredient = resultSet.getString("ingredient");

                    if (currentMeal == null || currentMeal.getMealId() != mealId) {
                        if (currentMeal != null) {
                            meals.add(currentMeal);
                        }
                        currentMeal = new Meal(mealId, mealName, category, new ArrayList<>());
                    }
                    currentMeal.getIngredients().add(ingredient);
                }
                if (currentMeal != null) {
                    meals.add(currentMeal);
                }
            }
        }
        return meals;
    }


    public void clearPlan() throws SQLException {
        String query = "DELETE FROM plan";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        }
    }


    public void addPlanEntry(String day, String category, int mealId) throws SQLException {
        String query = "INSERT INTO plan (day, category, meal_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, day);
            stmt.setString(2, category);
            stmt.setInt(3, mealId);
            stmt.executeUpdate();
        }
    }


    public List<PlanEntry> getPlan() throws SQLException {
        List<PlanEntry> planEntries = new ArrayList<>();
        String query = "SELECT p.day, p.category, m.meal " +
                "FROM plan p " +
                "JOIN meals m ON p.meal_id = m.meal_id " +
                "ORDER BY p.day, m.meal";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String day = rs.getString("day");
                String category = rs.getString("category");
                String mealName = rs.getString("meal");
                planEntries.add(new PlanEntry(day, category, mealName));
            }
        }
        return planEntries;
    }

    public Map<String, Integer> generateShoppingList() throws SQLException {
        Map<String, Integer> shoppingList = new HashMap<>();

        String query = "SELECT i.ingredient FROM plan p " +
                "JOIN meals m ON p.meal_id = m.meal_id " +
                "JOIN ingredients i ON m.meal_id = i.meal_id";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String ingredient = rs.getString("ingredient");
                shoppingList.put(ingredient, shoppingList.getOrDefault(ingredient, 0) + 1);
            }
        }
        return shoppingList;
    }

    public boolean isPlanReady() throws SQLException {
        String query = "SELECT COUNT(*) FROM plan";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1) > 0;    // if plan exists then return true
            }

        }
        return false;
    }


    public static class PlanEntry {
        public String day;
        public String category;
        public String mealName;

        public PlanEntry(String day, String category, String mealName) {
            this.day = day;
            this.category = category;
            this.mealName = mealName;
        }
    }
}
