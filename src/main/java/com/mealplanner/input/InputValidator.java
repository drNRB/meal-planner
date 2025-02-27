package com.mealplanner.input;

import java.util.regex.Pattern;

public class InputValidator {
    public static boolean isValidCommand(String command) {
        return command.matches("add|show|plan|list plan|save|exit");
    }

    public static boolean isValidCategory(String category) {
        return category.equals("breakfast") || category.equals("lunch") || category.equals("dinner");
    }

    public static boolean isValidName(String input) {
        String trimmed = input.trim();
        if (trimmed.endsWith(",")) {
            return false; //
        }

        return Pattern.matches("^[a-zA-Z ]+$", trimmed);
    }


    public static boolean isValidIngredient(String ingredient) {
        String trimmed = ingredient.trim();
        if (trimmed.isEmpty()) {
            return false;
        }


        if (trimmed.endsWith(",")) {
            return false;
        }

        return Pattern.matches("^[a-zA-Z]+$",trimmed);
    }
}