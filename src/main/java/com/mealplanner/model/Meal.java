package com.mealplanner.model;

import java.util.ArrayList;
import java.util.List;

public class Meal {
    private int mealId;
    private String name;
    private String category;
    private List<String> ingredients;



    public Meal(String name, String category, List<String> ingredients) {
        this.name = name;
        this.category = category;
        this.ingredients = new ArrayList<>();
        for(String ingredient : ingredients){
            this.ingredients.add(ingredient.trim());
        }
    }

    public Meal(int mealId, String name, String category, List<String> ingredients) {
        this(name, category, ingredients);
        this.mealId = mealId;
    }

    public int getMealId() {
        return mealId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
}