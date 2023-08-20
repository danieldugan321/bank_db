package bankfold;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private String username;
    private String password;


    public LoginFrame() {
        setTitle("Banking System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(400, 250);
        setLocationRelativeTo(null);

        // Create UI components
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPanel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        JButton loginButton = new JButton("Login");

        JLabel registerLabel = new JLabel("Don't have an account?");
        JButton registerButton = new JButton("Register");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerLabel);
        buttonPanel.add(registerButton);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);


        // Add action listener to the login button
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);

            try {
                // Retrieve data from the DatabaseHandler
                DatabaseHandler databaseHandler = new DatabaseHandler(username, password);
                String hashedPassword = databaseHandler.getHashedPassword();

                if (hashedPassword != null && BCrypt.checkpw(password, hashedPassword)) { // Check if the user exists & passwords match
                   // Check if the user's password is valid
                    try {
                        UserInterface.initializeUserInterface(databaseHandler);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                } else {
                        // Show an error message if the user is invalid
                        JOptionPane.showMessageDialog(null, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                    }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        // Add action listener to the register button
        registerButton.addActionListener(e -> {
            // Open the registration window
            RegistrationHandler registrationHandler = new RegistrationHandler();
            registrationHandler.setVisible(true);
        });
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void addLoginListener(LoginListener listener) {
        listener.onLogin(username, password);
    }
    public interface LoginListener {
        void onLogin(String username, String password);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(); // Create an instance of the LoginFrame class
            loginFrame.setVisible(true); // Show the login frame
        });
    }
}