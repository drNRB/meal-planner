# Meal Planner

Meal Planner is a Java application that allows users to manage their meals, plan a weekly menu, and generate shopping lists based on the planned meals. The application uses a PostgreSQL database to store information about meals, ingredients, and plans.
The project was based on the hyperskill course.

## Features

- **Add Meals**: Users can add new meals by specifying their category (breakfast, lunch, dinner), name, and list of ingredients.
- **Show Meals**: View saved meals categorized by type.
- **Weekly Planning**: Create a meal plan for each day of the week (Monday to Sunday) across all categories.
- **List Plans**: Display the current meal plan in a readable format.
- **Shopping List**: Generate and save a shopping list based on planned meals to a text file.
- **Input Validation**: Ensure the correctness of entered data (e.g., names and ingredients must contain only letters).

## Requirements

- **Java**: Version 11 or higher.
- **PostgreSQL**: PostgreSQL database (tested on version 13+).
- **Maven**: Project build system (installed locally).

## Installation

1. **Clone the Repository**:
```bash
   git clone https://github.com/[your-username]/meal-planner.git
   cd meal-planner
   ```
2. **Configure the Database**:
   - Create a PostrgeSQL database named meals_db
   - Update the connection details in DatabaseConnection.java if you want use different values than the defaults:
   ```java
     private static final String DB_URL = "jdbc:postgresql://localhost:5432/meals_db";
     private static final String USER = "postgres";
     private static final String PASS = "1111";
   
  3. **Build the Project**:
     ```bash
       mvn clean install

  4. **Run the Application**:
     ```bash
     mvn exec:java -Dexec.mainClass="mealplanner.Main"

  ## Usage

Upon launching the application, users can choose from the following commands:

- `add` – Add a new meal.
- `show` – Display meals for a selected category.
- `plan` – Plan meals for the week.
- `list plan` – Show the current meal plan.
- `save` – Generate and save a shopping list to a file.
- `exit` – Close the application.

  Example Usage:
  
  ### Adding a Meal
      ```
      What would you like to do (add, show, plan, list plan, save, exit)?
      add
      Which meal do you want to add (breakfast, lunch, dinner)?
      breakfast
      Input the meal's name:
      Pancakes
      Input the ingredients:
      flour, milk, eggs
      The meal has been added!

 
  ### Planning the Week:
      
      What would you like to do (add, show, plan, list plan, save, exit)?
      plan
      Monday
      Pancakes
      Omelette
      Choose the breakfast for Monday from the list above:
      Pancakes

  ### Saving a shopping:
      What would you like to do (add, show, plan, list plan, save, exit)?
      save
      Input a filename:
      shopping_list.txt
      Saved!


  ## Project Structure

- `src/main/java/mealplanner/` – Application source code:
  - `Main.java` – Application entry point.
  - `Meal.java` – Class representing a meal.
  - `MenuManager.java` – Main application logic.
  - `DatabaseConnector.java` – Database connection handling.
  - `UserInputHandler.java` – User input processing.
  - `InputValidator.java` – Input data validation.
  - `MenuDisplay.java` – Data display utilities.
  - `MealStorage.java` – Meal storage (optional, if not fully replaced by database).
- `pom.xml` – Maven configuration file.
- `src/test/` – Unit and integration tests (described below).

  ## Tests

The project includes unit and integration tests located in the `src/test/` directory. The tests verify:

- Input validation (`InputValidator`).
- Meal addition and retrieval logic (`DatabaseConnector`).
- Shopping list generation and planning (`MenuManager`).

To run the tests:

```bash
mvn test
