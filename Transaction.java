package bankfold;
//fixing transaction for viewtransactionpanel
public class Transaction {
    private int transactionId;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private double amount;
    private String transactionType;
    private String transactionDate;

    public Transaction(int transactionId, String sourceAccountNumber, String destinationAccountNumber, double amount, String transactionType, String transactionDate) {
        this.transactionId = transactionId;
        this.sourceAccountNumber = sourceAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
    }

    public int getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }
    public String setSourceAccountNumber(String sourceAccountNumber) {
        return this.sourceAccountNumber = sourceAccountNumber;
    }
    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }
    public double getAmount() {
        return amount;
    }
    public double setAmount(double amount) {
        return this.amount = amount;
    }
    public String getTransactionType() {
        return transactionType;
    }
    public String setTransactionType(String transactionType) {
        return this.transactionType = transactionType;
    }
    public String getTransactionDate() {
        return transactionDate;
    }
    public String setTransactionDate(String transactionDate) {
        return this.transactionDate = transactionDate;
    }
    // ...

    // Optionally, override the toString() method for convenient printing of transaction details
    @Override
    public String toString() {
        return "Transaction ID: " + transactionId + "\n" +
                "Source Account Number: " + sourceAccountNumber + "\n" +
                "Destination Account Number: " + destinationAccountNumber + "\n" +
                "Amount: " + amount + "\n" +
                "Transaction Type: " + transactionType + "\n" +
                "Transaction Date: " + transactionDate + "\n";
    }
}
