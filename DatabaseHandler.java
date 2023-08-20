package bankfold;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

// This class will provide methods for creating and closing database connections,
// executing update queries (INSERT, UPDATE, DELETE), and executing select queries (SELECT).
// You can use these methods to interact with the MySQL database in the application.
public class DatabaseHandler {
    private final String username;
    private final String password;

    public DatabaseHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }
    // This method will return true if the user is valid, and false if the user is invalid. Made to support logic around the login process.
    public boolean isValidUser() throws SQLException {
        Connection connection = null;
        boolean isValid = false;

        try {
            connection = DatabaseConnector.getConnection();
            String storedHashedPassword = getHashedPassword();

            if (storedHashedPassword != null) {
                isValid = BCrypt.checkpw(password, storedHashedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnector.closeConnection(connection);
        }

        return isValid;
    }


    public void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }
    public String getUserId() throws SQLException {
        String userId = null;  // Declare userId here
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnector.getConnection();
            String query = "SELECT user_id FROM users WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, getHashedPassword());
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getString("user_id");
            }
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            DatabaseConnector.closeConnection(connection);
        }

        return userId;
    }
    public String getUserIdByUsername(String username) throws SQLException { // This method will return the user ID for a given username, used for recipient money transfers
        String userId = null;  // Declare userId here
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnector.getConnection();
            String query = "SELECT user_id FROM users WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getString("user_id");
            }
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            DatabaseConnector.closeConnection(connection);
        }

        return userId;
    }
    public List<String> getAccountNumbers(String userId) throws SQLException {
        List<String> accountNumbers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnector.getConnection();
            String query = "SELECT accounts.account_number FROM accounts INNER JOIN users ON accounts.user_id = users.user_id WHERE users.user_id = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String accountNumber = resultSet.getString("account_number");
                accountNumbers.add(accountNumber);
            }
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            DatabaseConnector.closeConnection(connection);
        }

        return accountNumbers;
    }
    public String getUsername(String userId) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String username = null;

        try {
            connection = DatabaseConnector.getConnection();
            String query = "SELECT username FROM users WHERE user_id = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                username = resultSet.getString("username");
            }
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            DatabaseConnector.closeConnection(connection);
        }

        return username;
    }
    public double getAccountBalance(String accountNumber) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        double balance = 0;

        try {
            connection = DatabaseConnector.getConnection();
            String query = "SELECT balance FROM accounts WHERE account_number = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, accountNumber);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                balance = resultSet.getDouble("balance");
            }
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            DatabaseConnector.closeConnection(connection);
        }

        return balance;
    }
    public List<Transaction> getTransactionHistory(String userId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnector.getConnection();
            String query = "SELECT * FROM transactions WHERE user_id = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            resultSet = statement.executeQuery();

            // Process the transaction history
            while (resultSet.next()) {
                int transaction_id = resultSet.getInt("transaction_id");
                String source_account_number = resultSet.getString("source_account_number");
                String destination_account_number = resultSet.getString("destination_account_number");
                double amount = resultSet.getDouble("amount");
                String type = resultSet.getString("transaction_type");
                String date = resultSet.getString("transaction_date");

                Transaction transaction = new Transaction(transaction_id, source_account_number, destination_account_number, amount, type, date);
                transactions.add(transaction);
            }
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            DatabaseConnector.closeConnection(connection);
        }

        return transactions;
    }

    public String getHashedPassword() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String hashedPassword = null;

        try {
            connection = DatabaseConnector.getConnection();
            String query = "SELECT password FROM users WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // Retrieve the hashed password from the database
                    hashedPassword = resultSet.getString("password");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Close database resources
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                closeResultSet(resultSet);
                closeStatement(statement);
                DatabaseConnector.closeConnection(connection);
            }

            return hashedPassword;
        }

    public boolean addAccount(String userId, String accountNumber, double startingBalance) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        boolean success = false;

        try {
            connection = DatabaseConnector.getConnection();
            String query = "INSERT INTO accounts (user_id, account_number, balance) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            statement.setString(2, accountNumber);
            statement.setDouble(3, startingBalance);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 1) {
                success = true;
            }
        } finally {
            closeStatement(statement);
            DatabaseConnector.closeConnection(connection);
        }

        return success;
    }
}
