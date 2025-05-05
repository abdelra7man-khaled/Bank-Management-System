import enums.UserAction;
import interfaces.Profits;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Saving class extends Account and uses interface Profits
class Saving extends Account implements Profits {
    private static final double INTEREST_RATE = 0.14;  // 14%

    private List<Integer> balanceHistory;

    public Saving(int accountNumber, int balance, User owner) {
        super(accountNumber, balance, owner);
        this.balanceHistory = new ArrayList<>();
        balanceHistory.add(balance);  // Initial balance for the year
    }

    public Saving(int accountNumber, int balance, User owner, int cvv, LocalDate expDate) {
        super(accountNumber, balance, owner, cvv, expDate);
        this.balanceHistory = new ArrayList<>();
        balanceHistory.add(balance);  // Initial balance for the year
    }

    public void deposit(int amount) {
        // Update the balance and history
        setBalance(getBalance() + amount);
        balanceHistory.add(getBalance());
    }


    /*
    The profit calculation is based on the lowest balance in the account's history for the year and a constant interest rate of 14%.
    The lowest balance is retrieved by calling the findLowestBalance() method. The profit is then calculated by multiplying the lowest balance with the interest rate.
     */
    public void Profit_gain(UserAction actionType) {
        if (actionType == UserAction.PROFIT_GAIN) {
            int lowestBalance = findLowestBalance();
            double profit = lowestBalance * INTEREST_RATE;

            // Update the balance and history
            deposit((int) profit);

            System.out.println("Profit gain: " + profit);
            System.out.println("New balance: " + getBalance());
        }
    }

    private int findLowestBalance() {
        // Find the lowest balance in the year
        int lowest = Integer.MAX_VALUE;
        for (int balance : balanceHistory) {
            if (balance < lowest) {
                lowest = balance;
            }
        }
        return lowest;
    }
}
