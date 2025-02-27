package com.mealplanner.input;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserInputHandler {
    private Scanner scanner;

    public UserInputHandler() {
        this.scanner = new Scanner(System.in);
    }

    public String promptCommand() {
        while (true) {
            System.out.println("What would you like to do (add, show, plan, list plan, save, exit)?");
            String command = scanner.nextLine().trim().toLowerCase();
            if (InputValidator.isValidCommand(command)) {
                return command;
            }
        }
    }

    public String promptCategory() {
        while (true) {
            System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
            String command = scanner.nextLine().trim().toLowerCase();
            if (InputValidator.isValidCategory(command)) {
                return command;
            }
            System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
        }
    }

    public String promptName() {
        while (true) {
            System.out.println("Input the meal's name: ");
            String name = scanner.nextLine().trim();
            if (InputValidator.isValidName(name)) {
                return name;
            }
            System.out.println("Wrong format. Use letters only!");
        }
    }

    public List<String> promptIngredients() {
        while (true) {
            System.out.println("Input the ingredients:");
            String input = scanner.nextLine();
            String[] parts = input.split(",");
            List<String> ingredients = new ArrayList<>();
            boolean allValid = true;

            for (String part : parts) {
                String ingredient = part.trim();
                if (ingredient.isEmpty() || !ingredient.matches("[a-zA-Z ]+")) {
                    allValid = false;
                    break;
                }
                ingredients.add(ingredient);
            }

            if (allValid && !ingredients.isEmpty()) {
                return ingredients;
            } else {
                System.out.println("Wrong format. Use letters only!");
            }
        }
    }

    public String promptCategoryShow() {
        while (true) {
            System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
            String category = scanner.nextLine().trim().toLowerCase();
            if (InputValidator.isValidCategory(category)) {
                return category;
            }
            System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
        }
    }

    public String readLine() {
        return scanner.nextLine().trim();
    }
}