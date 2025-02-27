package com.mealplanner;

import com.mealplanner.service.MenuManager;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        MenuManager menuManager = new MenuManager();
        menuManager.run();
    }


}
