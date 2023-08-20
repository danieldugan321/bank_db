package bankfold;

import javax.swing.*;
import java.sql.SQLException;

public class Main {
    // Develop the main class to handle the flow of the program.
    // Instantiate objects, handle user input, and interact with the database using the defined classes and methods.
    public static void main(String[] args) {
        try {
            DatabaseConnector.getConnection(); // connect to the database
        } catch (SQLException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(); // Call loginframe.java
            loginFrame.setVisible(true);
        });
    }
}
