package com.mealplanner.ui;

import com.mealplanner.model.Meal;

import java.util.List;

public class MenuDisplay {
    public static void displayMeals(String category, List<Meal> meals) {
        System.out.println("Category: " + category);
        for (Meal meal : meals) {
            System.out.println("\nName: " + meal.getName());
            System.out.println("Ingredients:");
            for (String ingredient : meal.getIngredients()) {
                System.out.println(ingredient);
            }
        }
        System.out.println();


    }

    public static void displayNoMeals() {
        System.out.println("No meals saved. Add a meal first.");
    }
}