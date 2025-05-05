import enums.UserAction;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class Transaction {
    protected String Time_Stamp;
    protected int Transaction_Id;
    protected Account Source_Account;
    protected Account Destination_Account;
    protected int Amount_Money;
    protected UserAction transactionType;

    public boolean Transaction_State;
    private List<User> userList = new ArrayList<>();
    private String destination_AccountNumber;

    public Transaction(int transactionId, Account sourceAccount, Account destinationAccount,
                       int amountMoney, UserAction transactionType, List<User> userList) {
        this.Time_Stamp = generateTimeStamp();
        this.Transaction_Id = transactionId;
        this.Source_Account = sourceAccount;
        this.Destination_Account = destinationAccount;
        this.Amount_Money = amountMoney;
        this.transactionType = transactionType;
        this.Transaction_State = true;
        this.userList = readUsersFromFile("client.txt");
    }


    public void Transaction_Process(UserAction actionType) {
        if (actionType == UserAction.TRANSACTION_PROCESS) {
            // Specific logic for processing a transaction
            if (transactionType == UserAction.TRANSFER) {
                performTransfer();
            } else if (transactionType == UserAction.WITHDRAWAL) {
                performWithdrawal();
            } else if (transactionType == UserAction.DEPOSIT) {
                performDeposit();
            }
        }
    }

    private Account findAccountByNumber(String accountNumber, List<User> users) {
        for (User user : users) {
            if (user instanceof Client client) {
                List<Account> accounts = client.getAccounts();

                for (Account account : accounts) {
                    if (String.valueOf(account.getAccountNumber()).equals(accountNumber)) {
                        System.out.println("Found account: " + accountNumber);
                        return account;
                    }
                }
            }
        }
        System.out.println("Account not found: " + accountNumber);
        return null;  // Return null if the account is not found
    }


    private void performTransfer() {
        // Logic for transfer
        System.out.println("Processing transfer...");

        // Get the source account
        Account sourceAccount = getSource_Account();
        getDestination_Account();

        // Check if the source account is not null
        if (sourceAccount != null) {
            // Ask the user for the destination account number
            System.out.print("Confirm your destination account number: ");
            Scanner scanner = new Scanner(System.in);
            String destinationAccountNumber = scanner.nextLine();

            destination_AccountNumber = destinationAccountNumber;
            // Check if the entered destination account number is not empty
            if (!destinationAccountNumber.isEmpty()) {
                // Find the destination account based on the entered account number
                Account destinationAccount = loadAccountFromUserInput(destinationAccountNumber);

                // Check if the destination account was found
                if (destinationAccount != null) {
                    // Check if the source account has sufficient balance
                    if (sourceAccount.getBalance() >= getAmount_Money()) {
                        // Deduct money from the source account
                        sourceAccount.setBalance(sourceAccount.getBalance() - getAmount_Money());

                        // Add money to the destination account
                        destinationAccount.setBalance(destinationAccount.getBalance() + getAmount_Money());

                        System.out.println("Transfer successful!");

                        // Update balances in the file
                        updateBalanceInFile(sourceAccount);
                        updateBalanceInFile(destinationAccount);

                        // Set Transaction_State to true since the transfer was successful
                        Transaction_State = true;
                    } else {
                        System.out.println("Error: Insufficient funds for transfer. Transaction failed.");

                        // Set Transaction_State to false since the transfer failed
                        Transaction_State = false;
                    }
                } else {
                    System.out.println("Error: Destination account not found.");

                    // Set Transaction_State to false since the transfer failed
                    Transaction_State = false;
                }
            } else {
                System.out.println("Error: Destination account number is empty.");

                // Set Transaction_State to false since the transfer failed
                Transaction_State = false;
            }
        } else {
            System.out.println("Error: Source account is null.");

            // Set Transaction_State to false since the transfer failed
            Transaction_State = false;
        }
    }



    private void performWithdrawal() {

        Account sourceAccount = getSource_Account();

        // Logic for withdrawal
        System.out.println("Processing withdrawal...");

        // Get the source client
        Client sourceClient = (Client) sourceAccount.getOwner();

        if (sourceClient != null) {
            // Display account options to the user
            System.out.println("Select the account for withdrawal:");
            System.out.println("1. Savings");
            System.out.println("2. Current");

            Scanner scanner = new Scanner(System.in);
            int accountChoice = scanner.nextInt();

            Account selectedAccount;

            switch (accountChoice) {
                case 1:
                    selectedAccount = sourceClient.getSavingAccount();
                    break;
                case 2:
                    selectedAccount = sourceClient.getCurrentAccount();
                    break;
                default:
                    System.out.println("Invalid choice. Withdrawal failed.");
                    return;
            }

            // Get the withdrawal amount from the user
            System.out.print("Confirm your withdrawal amount: ");
            int withdrawalAmount = scanner.nextInt();

            // Check if the selected account has sufficient balance
            if (selectedAccount.getBalance() >= withdrawalAmount) {
                // Deduct money from the selected account
                selectedAccount.setBalance(selectedAccount.getBalance() - withdrawalAmount);

                System.out.println("Withdrawal successful from " + selectedAccount.getClass().getSimpleName() + " account!");

                // Update balance in the file
                updateBalanceInFile(selectedAccount);
            } else {
                System.out.println("Insufficient funds for withdrawal. Transaction failed.");
            }
        } else {
            System.out.println("Error: Source account not found. Withdrawal failed.");
        }
    }


    private void performDeposit() {
        // Get the source account
        Account sourceAccount = getSource_Account();

        // Continue with the deposit logic
        System.out.println("Processing deposit...");

        // Check if the source account is not null
        if (sourceAccount != null) {
            // Get the source client
            Client sourceClient = (Client) sourceAccount.getOwner();

            // Display account options to the user
            System.out.println("Select the account for deposit:");
            System.out.println("1. Saving");
            System.out.println("2. Current");

            Scanner scanner = new Scanner(System.in);
            int accountChoice = scanner.nextInt();

            Account selectedAccount;

            // Determine the selected account based on user choice
            switch (accountChoice) {
                case 1:
                    selectedAccount = sourceClient.getSavingAccount();
                    break;
                case 2:
                    selectedAccount = sourceClient.getCurrentAccount();
                    break;
                default:
                    System.out.println("Invalid choice. Deposit failed.");
                    return;
            }

            // Check if the selected account is not null
            if (selectedAccount != null) {
                // Get the deposit amount from the user
                System.out.print("Confirm the deposit amount: ");
                int depositAmount = scanner.nextInt();

                // Check if the deposit amount is positive
                if (depositAmount > 0) {
                    // Deposit into the selected account
                    selectedAccount.setBalance(selectedAccount.getBalance() + depositAmount);
                    System.out.println("Deposit successful to " + selectedAccount.getClass().getSimpleName() + " account!");

                    // Update balance in the file
                    updateBalanceInFile(selectedAccount);
                } else {
                    System.out.println("Error: Invalid deposit amount. Deposit failed.");
                }
            } else {
                System.out.println("Error: Selected account not found. Deposit failed.");
            }
        } else {
            System.out.println("Error: Source account not found. Deposit failed.");
        }
    }



    // Add a method to load account details from user input or file
    private Account loadAccountFromUserInput(String accountNumber) {
        List<Client> clients = readClientsFromFile("client.txt");

        for (Client client : clients) {
            for (Account account : client.getAccountList()) {
                if (String.valueOf(account.getAccount_Number()).equals(accountNumber)) {
                    return account;
                }
            }
        }

        return null;  // Return null if the account is not found
    }

    private List<Client> readClientsFromFile(String fileName) {
        List<Client> clients = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    Client client = parseClientFromLine(line);
                    clients.add(client);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing data at line: " + line);
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }

        return clients;
    }

    private Client parseClientFromLine(String line) {
        String[] parts = line.split(",");

        int id = Integer.parseInt(parts[0].trim());
        String firstName = parts[1].trim();
        String lastName = parts[2].trim();
        String username = parts[3].trim();
        String password = parts[4].trim();

        int[] accountNumbers = Arrays.stream(parts[5].split(","))
                .mapToInt(Integer::parseInt)
                .toArray();

        int telephoneNumber = Integer.parseInt(parts[6].trim());
        String address = parts[7].trim();

        Client client = new Client(id, firstName, lastName, username, password,
                accountNumbers, telephoneNumber, address);

        int balanceIndex = 8;
        while (balanceIndex < parts.length - 3) {
            String accountType = parts[balanceIndex++].trim();
            int accountBalance = Integer.parseInt(parts[balanceIndex++].trim());
            int cvv = Integer.parseInt(parts[balanceIndex++].trim());
            String expDate = parts[balanceIndex++].trim();

            Account account = createAccount(accountType, accountNumbers, accountBalance, client, cvv, expDate);
            client.addAccount(account);
        }

        return client;
    }

    private Account createAccount(String accountType, int[] accountNumbers, int accountBalance, User user, int cvv, String expDate) {
        // Assuming accountNumbers is an array, you might want to choose one account number or modify the method accordingly
        int accountNumber = accountNumbers[0];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        TemporalAccessor temporalAccessor = formatter.parse(expDate);
        LocalDate expirationDate = YearMonth.from(temporalAccessor).atEndOfMonth();


        // Create the appropriate account type based on the provided information
        return switch (accountType) {
            case "Saving" -> new Saving(accountNumber, accountBalance, user, cvv, expirationDate);
            case "Current" ->
                    new Current(accountNumber, accountBalance, user, cvv, expirationDate, 3); // Assuming 3% fee
            // Add more cases for other account types if needed
            default -> throw new IllegalArgumentException("Unsupported account type: " + accountType);
        };
    }

    private void updateBalanceInFile(Account account) {
        try {
            // Read all lines from the file
            Path path = Paths.get("client.txt");
            List<String> lines = Files.readAllLines(path);

            // Find the line that corresponds to the account's account number
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");

                // Assuming the 6th field (index 5) contains the account number
                String accountNumber = parts[5].trim();

                if (accountNumber.equals(String.valueOf(account.getAccount_Number()))) {
                    // Find the index of the balance field based on the account type
                    int balanceIndex = -1;

                    if (account instanceof Saving) {
                        // Find the index for Savings
                        balanceIndex = Arrays.asList(parts).indexOf("Saving");
                    } else if (account instanceof Current) {
                        // Find the index for Current
                        balanceIndex = Arrays.asList(parts).indexOf("Current");
                    }

                    // Update the balance field based on the account type
                    if (balanceIndex != -1) {
                        parts[balanceIndex + 1] = String.valueOf(account.getBalance()); // Assuming balance is the next field
                    } else {
                        System.out.println("Error: Unsupported account type. Balance not updated.");
                    }

                    // Update the line in the list
                    lines.set(i, String.join(",", parts));
                    break;
                }
            }

            // Write the modified lines back to the file
            Files.write(path, lines);
        } catch (IOException e) {
            System.err.println("Error updating balance in file: " + e.getMessage());
        }
    }


    private String generateTimeStamp() {
        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define a format for the timestamp (adjust as needed)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        // Format the date-time using the defined format
        return currentDateTime.format(formatter);
    }


    public void saveTransactionToFile() {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("transaction.txt", true)))) {
            String ownerDetails = (Source_Account != null && Source_Account.getOwner() != null)
                    ? Source_Account.getOwner().getUserName()
                    : "N/A";

            String sourceAccountDetails = (Source_Account != null)
                    ? Source_Account.getAccount_Number() +""
                    : "N/A";

            String destinationAccountDetails = (Destination_Account != null)
                    ? Destination_Account.getAccount_Number() +", Transferred to: " + Destination_Account.getOwner().getUserName() +""
                    : "N/A";

            String transactionDetails = "Time_Stamp: " + Time_Stamp +
                    ", Transaction_Id: " + Transaction_Id +
                    ", Owner: " + ownerDetails +
                    ", Source_Account: " + sourceAccountDetails +
                    ", Destination_Account: " + destinationAccountDetails +
                    ", Amount_Money: " + Amount_Money +
                    ", Transaction_Type: " + transactionType +
                    ", Transaction_State: " + Transaction_State;

            writer.println(transactionDetails);

        } catch (IOException e) {
            System.err.println("Error writing to transaction file: " + e.getMessage());
        }
    }

    private List<User> readUsersFromFile(String fileName) {
        List<User> userListFromFile = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    User user = parseUserFromLine(line);
                    userListFromFile.add(user);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing user data at line: " + line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading user data from file: " + e.getMessage());
        }

        return userListFromFile;
    }

    private User parseUserFromLine(String line) {
        String[] parts = line.split(",");

        int id = Integer.parseInt(parts[0].trim());
        String firstName = parts[1].trim();
        String lastName = parts[2].trim();
        String username = parts[3].trim();
        String password = parts[4].trim();

        // Assuming you have a 'Client' class, you can create an instance of it
        // and set the basic user information
        Client client = new Client(id, firstName, lastName, username, password);

        // Assuming the account numbers are stored as comma-separated values at index 5
        String[] accountNumbersStr = parts[5].split(",");
        int[] accountNumbers = Arrays.stream(accountNumbersStr)
                .mapToInt(Integer::parseInt)
                .toArray();

        // Set the account numbers to the client
        client.setAccount_Number(accountNumbers);

        // Assuming the telephone number is at index 6
        int telephoneNumber = Integer.parseInt(parts[6].trim());
        client.setTelephone_Number(telephoneNumber);

        // Assuming the address is at index 7
        String address = parts[7].trim();
        client.setAddress(address);

        // Assuming the rest of the data is related to accounts
        int balanceIndex = 8;
        while (balanceIndex < parts.length - 3) {
            String accountType = parts[balanceIndex++].trim();
            int accountBalance = Integer.parseInt(parts[balanceIndex++].trim());
            int cvv = Integer.parseInt(parts[balanceIndex++].trim());
            String expDate = parts[balanceIndex++].trim();

            // Create an account based on the account type and add it to the client
            Account account = createAccount(accountType, accountNumbers, accountBalance, client, cvv, expDate);
            client.addAccount(account);
        }

        return client;
    }


    public String getTime_Stamp() {
        return Time_Stamp;
    }

    public void setTime_Stamp(String time_Stamp) {
        Time_Stamp = time_Stamp;
    }

    public int getTransaction_Id() {
        return Transaction_Id;
    }

    public void setTransaction_Id(int transaction_Id) {
        Transaction_Id = transaction_Id;
    }

    public Account getSource_Account() {
        // If the source account is null, prompt the user to input the account details
        if (Source_Account == null) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the source account number: ");
            String sourceAccountNumber = scanner.next();

            // Load the account from the file or other source
            Source_Account = loadAccountFromUserInput(sourceAccountNumber);

            // Check if the source account is still null
            if (Source_Account == null) {
                System.out.println("Error: Source account not found.");
            }
        }
        return Source_Account;
    }

    public void setSource_Account(Account source_Account) {
        this.Source_Account = source_Account;
    }

    public Account getDestination_Account() {
        if (Destination_Account == null) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the Destination account number: ");
            String detinationAccountNumber = scanner.next();

            // Load the account from the file or other source
            Destination_Account = loadAccountFromUserInput(detinationAccountNumber);

            // Check if the source account is still null
            if (Destination_Account == null) {
                System.out.println("Error: Source account not found.");
            }
        }
        return Destination_Account;
    }

    public void setDestination_Account(Account destination_Account) {
        this.Destination_Account = destination_Account;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public int getAmount_Money() {
        return Amount_Money;
    }

    public void setAmount_Money(int amount_Money) {
        Amount_Money = amount_Money;
    }

    public UserAction getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(UserAction transactionType) {
        this.transactionType = transactionType;
    }

    public boolean isTransaction_State() {
        return Transaction_State;
    }

    public void setTransaction_State(boolean transaction_State) {
        Transaction_State = transaction_State;
    }
}