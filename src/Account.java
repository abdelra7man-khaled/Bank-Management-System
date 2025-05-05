import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Account {
    private int accountNumber;
    private int balance;
    private User owner;
    private int CVV;
    private LocalDate expDate;
    private boolean accountState;
    private Transaction transaction;
    private List<Transaction> transactions; // Use a list for multiple transactions

    private List<Account> accountList = new ArrayList<>();

    public Account(int accountNumber, int balance, User owner, int cvv, LocalDate expDate, boolean accountState) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.owner = owner;
        this.CVV = cvv;
        this.expDate = expDate;
        this.accountState = accountState;
        this.transactions = new ArrayList<>();

    }

    public Account(int accountNumber, int balance, User owner) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.owner = owner;
    }

    public Account(int accountNumber, int balance, User owner, int cvv, LocalDate expDate) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.owner = owner;
        this.CVV = cvv;
        this.expDate = expDate;
        this.accountState = true;
    }


    public LocalDate getExp_Date() {
        return expDate;
    }


    public String getAccount_Number() {
        return String.valueOf(accountNumber);
    }


    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public User getOwner() {
        return owner;
    }

    public boolean isAccount_State() {
        return accountState;
    }

    public int getCVV() {
        return CVV;
    }

}
