package bankfold;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class TransactionDirector {
    private final DatabaseHandler databaseHandler;

    public TransactionDirector(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public boolean transferFunds(String sourceAccountNumber, String destinationAccountNumber, double amount) throws SQLException {
        // Get the current balance of the source account
        double sourceAccountBalance = databaseHandler.getAccountBalance(sourceAccountNumber);

        // Check if the source account has enough funds
        if (sourceAccountBalance < amount) {
            return false;
        }
        // Get the user's current balance from the database
        Connection connection = null;
        PreparedStatement senderStatement = null;
        PreparedStatement receiverStatement = null;

        try {
            connection = DatabaseConnector.getConnection();
            String updateSenderQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            String updateReceiverQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

            //Start a transaction to ensure the integrity of both updates
            connection.setAutoCommit(false);

            // Update the sender's account
            senderStatement = connection.prepareStatement(updateSenderQuery);
            senderStatement.setDouble(1, amount);
            senderStatement.setString(2, sourceAccountNumber);
            senderStatement.executeUpdate();

            // Update the receiver's balance
            receiverStatement = connection.prepareStatement(updateReceiverQuery);
            receiverStatement.setDouble(1, amount);
            receiverStatement.setString(2, destinationAccountNumber);
            receiverStatement.executeUpdate();

            // Commit the transaction if both updates are successful
            connection.commit();

            // Call performTransaction to record the transaction
            performTransaction(
                    generateTransactionId(),
                    sourceAccountNumber,
                    destinationAccountNumber,
                    amount,
                    "transfer"
            );

            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Rollback the transaction if there's an issue with either update
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        } finally {
            // Restore auto-commit and close database resources
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
                if (senderStatement != null) {
                    senderStatement.close();
                }
                if (receiverStatement != null) {
                    receiverStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to send to another user
    public static boolean sendTo(String userId, String recipientUserId, double amount) {
        // Get the user's current balance from the database
        Connection connection = null;
        PreparedStatement senderStatement = null;
        PreparedStatement receiverStatement = null;

        try {
            connection = DatabaseConnector.getConnection();
            String updateSenderQuery = "UPDATE accounts SET balance = balance - ? WHERE user_id = ?";
            String updateReceiverQuery = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";

            //Start a transaction to ensure the integrity of both updates
            connection.setAutoCommit(false);

            // Update the sender's account
            senderStatement = connection.prepareStatement(updateSenderQuery);
            senderStatement.setDouble(1, amount);
            senderStatement.setString(2, userId);
            senderStatement.executeUpdate();

            // Update the receiver's balance
            receiverStatement = connection.prepareStatement(updateReceiverQuery);
            receiverStatement.setDouble(1, amount);
            receiverStatement.setString(2, recipientUserId);
            receiverStatement.executeUpdate();

            // Commit the transaction if both updates are successful
            connection.commit();

            // Call performTransaction to record the transaction
            performTransaction(
                    generateTransactionId(),
                    userId,
                    recipientUserId,
                    amount,
                    "transfer"
            );

            // Return true to indicate success
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Rollback the transaction if there's an issue with either update
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // Return false to indicate failure
            return false;
        } finally {
            // Restore auto-commit and close database resources
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
                if (senderStatement != null) {
                    senderStatement.close();
                }
                if (receiverStatement != null) {
                    receiverStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static void performTransaction(String transaction_id, String source_account_number, String destination_account_number, double amount, String transaction_type) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnector.getConnection(); // CHECK DATA TYPE AND CHANGE
            String query = "INSERT INTO transactions (transaction_id, source_account_number, destination_account_number, amount, transaction_type) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(2, transaction_id);
            statement.setString(3, source_account_number);
            statement.setString(4, destination_account_number);
            statement.setDouble(5, amount);
            statement.setString(6, transaction_type);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Transaction successful!");
            } else {
                System.out.println("Transaction failed. Please try again.");
            }
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
    }

    // Method to generate a random 4-digit transaction ID
    private String generateTransactionId() {
        Random random = new Random();
        int transactionId = 1000 + random.nextInt(9000);
        return String.valueOf(transactionId);
    }
}
