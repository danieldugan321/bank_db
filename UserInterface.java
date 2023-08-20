package bankfold;

import javax.swing.*;

import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.Random;

public class UserInterface extends JFrame {
    private final JPanel cardPanel;
    private final CardLayout cardLayout;
    private final DatabaseHandler databaseHandler;
    private final TransactionDirector transactionDirector;
        // Create a user interface for the banking system using Java's Swing or JavaFX.
        // Implement forms and screens for user registration, login, account details, transaction history, and fund transfers.

    public UserInterface(DatabaseHandler databaseHandler, String userId) {
        this.databaseHandler = databaseHandler; // Initialize databaseHandler later when needed
        this.transactionDirector = new TransactionDirector(databaseHandler); // Initialize transactionDirector later when needed

        setTitle("Banking System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        // Create a panel with CardLayout to hold different panels
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        // Create the menu panel
        JPanel menuPanel = createMenuPanel(userId);
        cardPanel.add(menuPanel, "menu");
        // Add the cardPanel to the JFrame
        add(cardPanel);

        // Show the menu panel initially
        cardLayout.show(cardPanel, "menu");
    }

    private JPanel createMenuPanel(String userId) {
        // Create the menu panel with buttons for various actions
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(6, 1, 10, 10));
        // Create button for different actions
        JButton viewProfileButton = new JButton("View Profile");
        JButton viewBalanceButton = new JButton("View Account Balance");
        JButton viewTransactionButton = new JButton("View Transaction History");
        JButton transferFundsButton = new JButton("Transfer Funds");
        JButton addAccountButton = new JButton("Add Account");
        JButton logoutButton = new JButton("Logout");


        // Add action listener to the viewProfileButton
        viewProfileButton.addActionListener(e -> {
            JPanel viewProfilePanel = createViewProfilePanel(userId);
            cardPanel.add(viewProfilePanel, "viewProfile");
            cardLayout.show(cardPanel, "viewProfile");
        });
        // Add action listener to the viewBalanceButton
        viewBalanceButton.addActionListener(e -> {
            JPanel viewBalancePanel = createViewBalancePanel();
            cardPanel.add(viewBalancePanel, "viewBalance");
            cardLayout.show(cardPanel, "viewBalance");
        });

        // Add action listener to the viewTransactionButton
        viewTransactionButton.addActionListener(e -> {
            JPanel viewTransactionPanel = createViewTransactionPanel(userId);
            cardPanel.add(viewTransactionPanel, "viewTransaction");
            cardLayout.show(cardPanel, "viewTransaction");
        });

        // Add action listener to the transferFundsButton
        transferFundsButton.addActionListener(e -> {
            JPanel transferFundsPanel = createTransferFundsPanel(userId);
            cardPanel.add(transferFundsPanel, "transferFunds");
            cardLayout.show(cardPanel, "transferFunds");
        });

        // Add action listener to the addAccountButton
        addAccountButton.addActionListener(e -> {
            JPanel viewAddAccPanel = createViewAddAccPanel(userId);
            cardPanel.add(viewAddAccPanel, "addAccount");
            cardLayout.show(cardPanel, "addAccount");
        });

        // Add action listener to the logoutButton
        logoutButton.addActionListener(e -> {
            // Close the application
            System.exit(0);
        });

        // Add the buttons to the menuPanel
        menuPanel.add(viewProfileButton);
        menuPanel.add(viewBalanceButton);
        menuPanel.add(viewTransactionButton);
        menuPanel.add(transferFundsButton);
        menuPanel.add(addAccountButton);
        menuPanel.add(logoutButton);

        return menuPanel;
    }
    private JPanel createViewProfilePanel(String userId) {
        JPanel viewProfilePanel = new JPanel();
        viewProfilePanel.setLayout(new GridLayout(4, 1));

        // Create a label to show the profile
        JLabel profileNameLabel = new JLabel(""); // Change this to show the actual profile name
        profileNameLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        profileNameLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create a button to go back to the menu
        JButton backButton = new JButton("Back");

        // Add the label and button to the viewProfilePanel
        viewProfilePanel.add(profileNameLabel);
        viewProfilePanel.add(backButton);

        // Add action listener to the backButton
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "menu"));

        try {
            // Get the user's profile information from the database using the databaseHandler
            String profileName = databaseHandler.getUsername(userId);
            System.out.println("Profile name: " + profileName);//debugging line
            System.out.println("User ID: " + userId);//debugging line

            profileNameLabel.setText((profileName != null) ? ("Welcome, " + profileName) : "Profile not found");
        } catch (SQLException ex) {
            ex.printStackTrace();
            profileNameLabel.setText("Error Retrieving Results");
            //use prepared statements to retrieve the profile information from the database
            //display the profile information
        }
        return viewProfilePanel;
    }
    private JPanel createViewAddAccPanel(String userId) {
        JPanel viewAddAccPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // Create a label to show the profile
        JLabel profileNameLabel = new JLabel("Welcome, "); // Change this to show the actual profile name
        profileNameLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        viewAddAccPanel.add(profileNameLabel, gbc);

        // Create a label to show the account number
        JLabel accountNumberLabel = new JLabel("Account Number: ");
        JTextField accountNumberField = new JTextField(15);
        accountNumberField.setEditable(false);
        accountNumberField.setText(generateAccountNumber()); // Generate account number and display it
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        viewAddAccPanel.add(accountNumberLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        viewAddAccPanel.add(accountNumberField, gbc);

        // Create a label and text field for starting balance
        JLabel balanceLabel = new JLabel("Starting Balance: ");
        JTextField balanceField = new JTextField(10);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        viewAddAccPanel.add(balanceLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        viewAddAccPanel.add(balanceField, gbc);

        // Create a button to add the account
        JButton addAccountButton = new JButton("Add Account");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        viewAddAccPanel.add(addAccountButton, gbc);

        // Create a label to show the result message
        JLabel resultLabel = new JLabel("");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        viewAddAccPanel.add(resultLabel, gbc);

        // Create a button to go back to the menu
        JButton backButton = new JButton("Back");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        viewAddAccPanel.add(backButton, gbc);

        // Add action listener to the addAccountButton
        addAccountButton.addActionListener(e -> {
            double balance = Double.parseDouble(balanceField.getText());

            try {
                boolean success = databaseHandler.addAccount(userId, accountNumberField.getText(), balance);

                if (success) {
                    resultLabel.setText("Account added successfully");
                    resultLabel.setForeground(Color.GREEN);
                } else {
                    resultLabel.setText("Error adding account");
                    resultLabel.setForeground(Color.RED);
                }

                // clear the input field
                balanceField.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                resultLabel.setText("Error adding account");
                resultLabel.setForeground(Color.RED);
            }
        });
            // Add action listener to the backButton
            backButton.addActionListener(event -> cardLayout.show(cardPanel, "menu"));

            return viewAddAccPanel;
    }

    private JPanel createViewBalancePanel() {
        JPanel viewBalancePanel = new JPanel();
        viewBalancePanel.setLayout(new GridLayout(2, 1));

        // Create a label to show the balance
        JLabel balanceLabel = new JLabel(); // Change this to show the actual balance
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        balanceLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create a button to go back to the menu
        JButton backButton = new JButton("Back");

        // Add the label and button to the viewBalancePanel
        viewBalancePanel.add(balanceLabel);
        viewBalancePanel.add(backButton);

        // Add action listener to the backButton
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "menu"));

        try {
            String userId = databaseHandler.getUserId();
            List<String> accountNumbers = databaseHandler.getAccountNumbers(userId);

            // Loop through each account number and display its balance
            for (String accountNumber : accountNumbers) {
                double balance = databaseHandler.getAccountBalance(accountNumber);
                JLabel accountLabel = new JLabel("Account Number: " + accountNumber + " Balance: $" + balance);
                viewBalancePanel.add(accountLabel);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            balanceLabel.setText("Error Retrieving Balance");
        }
        return viewBalancePanel;
    }

    // This method retrieves the transaction data from the database using a prepared statement and adds it to the 'transactionListModel' to populate the transactionList component.
    private JPanel createViewTransactionPanel(String userId) {
        JPanel viewTransactionPanel = new JPanel();
        viewTransactionPanel.setLayout(new BorderLayout());

        // Create a label to show the transaction history
        JLabel transactionLabel = new JLabel("Transaction History"); // Change this to show the actual transaction history
        transactionLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        transactionLabel.setHorizontalAlignment(JLabel.CENTER);

        DefaultListModel<Transaction> transactionListModel = new DefaultListModel<>();
        JList<Transaction> transactionList = new JList<>(transactionListModel);

        // Check if there are transactions to display
        List<Transaction> transactions;
        try {
            transactions = databaseHandler.getTransactionHistory(userId);
        } catch (SQLException ex) {
            ex.printStackTrace();
            transactions = new java.util.ArrayList<>();
        }
        if (transactions.isEmpty()) {
            // Use the custom cell renderer for non-empty transactions
            transactionList.setCellRenderer(new TransactionListCellRenderer());
        } else {
            // Use the default cell renderer for empty transactions
            transactionList.setCellRenderer(new NoTransactionListCellRenderer());
        }
        // Create a button to go back to the menu
        JButton backButton = new JButton("Back");

        // Add the label and button to the viewTransactionPanel
        viewTransactionPanel.add(transactionLabel, BorderLayout.NORTH);
        viewTransactionPanel.add(new JScrollPane(transactionList), BorderLayout.CENTER);
        viewTransactionPanel.add(backButton, BorderLayout.SOUTH);

        // Add action listener to the backButton
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "menu"));

        return viewTransactionPanel;
    }

    private JPanel createTransferFundsPanel(String userId) {
        JPanel transferFundsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // Create labels and text fields for the account number and amount
        JLabel sourceAccountLabel = new JLabel("Source Account:");
        JComboBox<String> sourceAccountDropdown = new JComboBox<>();
        JLabel destinationAccountLabel = new JLabel("Destination Account:");
        JComboBox<String> destinationAccountDropdown = new JComboBox<>();

        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountTextfield = new JTextField();

        // Create a button to transfer funds
        JButton transferButton = new JButton("Transfer");

        // Create a button to go back to the menu
        JButton backButton = new JButton("Back");

        // Set grid positions for labels and text fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        transferFundsPanel.add(sourceAccountLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        transferFundsPanel.add(sourceAccountDropdown, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        transferFundsPanel.add(destinationAccountLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        transferFundsPanel.add(destinationAccountDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        transferFundsPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        transferFundsPanel.add(amountTextfield, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        transferFundsPanel.add(transferButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        transferFundsPanel.add(backButton, gbc);


        // Populate the accountDropdown with the user's accounts
        try {
            List<String> accountNumbers = databaseHandler.getAccountNumbers(userId);
            for (String accountNumber : accountNumbers) {
                sourceAccountDropdown.addItem(accountNumber);
                destinationAccountDropdown.addItem(accountNumber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Display an error message if there's an issue with database retrieval
            JOptionPane.showMessageDialog(transferFundsPanel, "Error retrieving accounts", "Error", JOptionPane.ERROR_MESSAGE);
            return transferFundsPanel;
        }
        // Add action listener to the transferButton
        transferButton.addActionListener(e -> {
            // Get the destination number and amount from the text fields
            String sourceAccountNumber = (String) sourceAccountDropdown.getSelectedItem();
            String destinationAccountNumber = (String) destinationAccountDropdown.getSelectedItem();
            double amount = Double.parseDouble(amountTextfield.getText());

            // Check if the destination account number is valid
            // You may want to add additional validation, such as checking if the account exists in the database

            if (sourceAccountNumber == null || destinationAccountNumber == null) {
                JOptionPane.showMessageDialog(transferFundsPanel, "Please select an account number", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Stop executing this method
            }


            if (sourceAccountNumber.equals(destinationAccountNumber)) {
                JOptionPane.showMessageDialog(transferFundsPanel, "Source and destination accounts cannot be the same", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Stop executing this method
            }
            if (sourceAccountNumber.isEmpty() || destinationAccountNumber.isEmpty()) {
                JOptionPane.showMessageDialog(transferFundsPanel, "Please enter an account number", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Stop executing this method
            }

            // Check if the amount is valid
            if (amount <= 0) {
                JOptionPane.showMessageDialog(cardPanel, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Stop executing this method
            }

            // Get the user's current balance from the database
            Connection connection = null;

            try {
                // Perform the fund transfer using TransactionDirector
                boolean success = transactionDirector.transferFunds(sourceAccountNumber, destinationAccountNumber, amount);

                if(success) {
                    JOptionPane.showMessageDialog(cardPanel, "Transfer successful", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Clear the input fields
                    amountTextfield.setText("");
                } else {
                    JOptionPane.showMessageDialog(cardPanel, "Transfer failed", "Error", JOptionPane.ERROR_MESSAGE);
                }

                // Clear the input fields
                amountTextfield.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Display an error message if there's an issue with database retrieval
                JOptionPane.showMessageDialog(cardPanel, "Error retrieving account balance", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Close database resources
                DatabaseConnector.closeConnection(connection);
            }
        });
        // Add action listener to the backButton
        backButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "menu");
        });

        return transferFundsPanel;
    }

    private JPanel createSendToPanel(String userId) {
        JPanel sendToPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // Create labels and text fields for recipient username and amount
        JLabel recipientLabel = new JLabel("Recipient Username:");
        JTextField recipientField = new JTextField();

        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();

        // Create a button to send funds
        JButton sendButton = new JButton("Send");

        // Create a button to go back to the menu
        JButton backButton = new JButton("Back");

        // Set grid positions for labels and text fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        sendToPanel.add(recipientLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        sendToPanel.add(recipientField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        sendToPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        sendToPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        sendToPanel.add(sendButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        sendToPanel.add(backButton, gbc);

        // Add action listener to the sendButton
        sendButton.addActionListener(e -> {
            String recipientUsername = recipientField.getText();
            double amount = Double.parseDouble(amountField.getText());

            try {
                // Retrieve recipient's user_id using DatabaseHandler
                String recipientUserId = databaseHandler.getUserIdByUsername(recipientUsername); // change this
                if (recipientUserId == null) {
                    JOptionPane.showMessageDialog(sendToPanel, "Recipient not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Perform fund transfer using TransactionDirector
                boolean success = transactionDirector.sendTo(userId, recipientUserId, amount); // change this
                if (success) {
                    JOptionPane.showMessageDialog(sendToPanel, "Fund transfer successful!");
                } else {
                    JOptionPane.showMessageDialog(sendToPanel, "Fund transfer failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                // Clear the input fields
                recipientField.setText("");
                amountField.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(sendToPanel, "An error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add action listener to the backButton
        backButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "menu");
        });

        return sendToPanel;
    }
    private class TransactionListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if(value instanceof Transaction) {
                Transaction transaction = (Transaction) value;

                // Create a label to display the transaction details
                JLabel label = new JLabel(transaction.toString());

                // Set the font size and style
                label.setFont(new Font("Arial", Font.PLAIN, 20));

                // Set the background and foreground color based on selection
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                } else {
                    label.setBackground(list.getBackground());
                    label.setForeground(list.getForeground());
                }

                // Set the border and opaque properties for better appearance
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setOpaque(true);

                return label;
            } else {
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        }
    }
    // Method to generate a random 8-digit account number
    private String generateAccountNumber() {
        Random random = new Random();
        int accountNumber = 10000000 + random.nextInt(90000000);
        return String.valueOf(accountNumber);
    }

    private class NoTransactionListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setText("No transactions");
            return label;
        }
    }
        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame(); // Create an instance of the login frame
                loginFrame.setVisible(true); // Show the login frame

                // Add action listener to the login button
                loginFrame.addLoginListener((username, password) -> {
                    try {
                        // Create a databaseHandler instance with the provided username and password
                        DatabaseHandler databaseHandler = new DatabaseHandler(username, password);

                        // Check if the provided username and password are valid
                        if (!databaseHandler.isValidUser()) {
                            JOptionPane.showMessageDialog(loginFrame, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                            // Stop executing this method
                        } else {

                            // Create an instance of the user interface
                            initializeUserInterface(databaseHandler);
                            loginFrame.dispose(); // Close the login frame
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
            });
        }
        public static void initializeUserInterface(DatabaseHandler databaseHandler) { // seperate method to initialize the user interface. Was previously in the main method but seperated to simplify userid and username passing
            try {
                String userId = databaseHandler.getUserId(); // get the user id from the database
                SwingUtilities.invokeLater(() -> {
                    UserInterface userInterface = new UserInterface(databaseHandler, userId); // create an instance of the user interface
                    userInterface.setVisible(true); // show the user interface
                });
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
}
