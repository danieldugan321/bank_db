package bankfold;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class RegistrationHandler extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    // storing the registered username and password in a variable
    private String registeredUsername;
    private String registeredPassword;
    // public boolean regini to be used by the UserInterface class
    public boolean regInitialized;

    public RegistrationHandler() {
        regInitialized = true;
        setTitle("Banking System - Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(400, 250);
        setLocationRelativeTo(null);

        // Create UI components

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPanel);

        JLabel introLabel = new JLabel("Don't have an account? Create one here!");
        introLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(confirmPasswordLabel);
        formPanel.add(confirmPasswordField);

        JButton registerButton = new JButton("Register");

        // Add action listener to the register button
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String userId = UUID.randomUUID().toString();

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match. Please try again.");
                passwordField.setText("");
                confirmPasswordField.setText("");
                return;
            }
            if (registerUser(userId, username, password)) {
                // Successful registration
                JOptionPane.showMessageDialog(null, "Registration successful!");
                registeredUsername = username;
                registeredPassword = password;
                openLoginFrame();
                // Open the login page or perform other actions
            } else {
                // Registration failed
                JOptionPane.showMessageDialog(null, "Registration failed. Please try again.");
            }
        });

        contentPanel.add(introLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(registerButton, BorderLayout.SOUTH);
    }

    private void openLoginFrame() {
        // Close the registration window
        dispose();
        // Open the login window and pass the registered username and password
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setUsername(registeredUsername);
        loginFrame.setPassword(registeredPassword);
        loginFrame.setVisible(true);
    }
    private boolean registerUser(String userId, String username, String password) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Hash the password
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            connection = DatabaseConnector.getConnection();
            String query = "INSERT INTO users (user_id, username, password) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            statement.setString(2, username);
            statement.setString(3, hashedPassword);
            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0; // If rows were affected, registration is successful

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close database resources
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            DatabaseConnector.closeConnection(connection);
        }

        return false; // Default to false if an exception occurs
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegistrationHandler registrationHandler = new RegistrationHandler();
            registrationHandler.setVisible(true);
        });
    }
}