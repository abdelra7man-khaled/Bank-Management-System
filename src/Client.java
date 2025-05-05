import enums.UserAction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

class Client extends User {
    private int[] Account_Number;
    private int Balance;
    protected int Telephone_Number;
    protected String Address;
    private Account clientAccount;
    private List<Account> accountList = new ArrayList<>();
    private static int transactionCounter = 1;
    private LocalDate lastTransactionDate;
    private User user;

    private List<User> userList;

    public Client(int ID, String FirstName, String LastName, String UserName, String Password,
                  int[] accountNumber, int balance, int telephoneNumber, String address) {
        super(ID, FirstName, LastName, UserName, Password);
        this.Telephone_Number = telephoneNumber;
        this.Address = address;
        this.Account_Number = accountNumber;
        this.Balance = balance;

    }

    public Client(int ID, String firstName, String lastName, String userName, String password
            , int[] account_Number, int telephoneNumber, String address) {
        super(ID, firstName, lastName, userName, password);
        this.Telephone_Number = telephoneNumber;
        this.Address = address;
        this.Account_Number = account_Number;

        // Retrieve the accounts for the user and update the balance
        updateBalance(getAccounts());
    }

    public Client(int clientID, String firstName, String lastName, String userName, String password,
                  int[] accountNumbers, int telephoneNumber, String address, List<Account> accounts) {
        super(clientID, firstName, lastName, userName, password);
        this.Account_Number = accountNumbers;
        this.Telephone_Number = telephoneNumber;
        this.Address = address;
        this.accountList = accounts;
        updateBalance(accounts);

    }

    public Client(int clientID, String firstName, String lastName, String userName, String password, List<Account> accounts, int telephoneNumber, String address) {
        super(clientID, firstName, lastName, userName, password);
        this.accountList = accounts;
        this.Telephone_Number = telephoneNumber;
        this.Address = address;
        updateBalance(accounts);

    }

    public Client(int id, String firstName, String lastName, String username, String password) {
        super(id, firstName, lastName, username, password);
        this.accountList = new ArrayList<>();
    }

    private void updateBalance(List<Account> accounts) {
        int totalBalance = accounts.stream().mapToInt(Account::getBalance).sum();
        setBalance(totalBalance);
    }


    public void Display_Acc_Details(UserAction actionType) {
        if (actionType == UserAction.DISPLAY_DETAILS) {
            Scanner scanner = new Scanner(System.in);

            // Find the client with the matching username
            Optional<Client> matchingClient = getClientByUsername(getUserName());

            if (matchingClient.isPresent()) {
                Client client = matchingClient.get();

                // Display all details for the client account
                System.out.println("Client Details:");
                System.out.println("ID: " + client.getID());
                System.out.println("First Name: " + client.getFirstName());
                System.out.println("Last Name: " + client.getLastName());
                System.out.println("Username: " + client.getUserName());
                System.out.println("Password: " + client.getPassword());
                System.out.println("Account Numbers: " + Arrays.toString(client.getAccount_Number()));
                System.out.println("Telephone Number: " + client.getTelephone_Number());
                System.out.println("Address: " + client.getAddress());
                System.out.println();

                // Display account details for each account
                System.out.println("Account Details:");
                for (Account account : client.getAccountList()) {
                    System.out.println("Account Type: " + account.getClass().getSimpleName());
                    System.out.println("Balance: " + account.getBalance());

                    // Check if the account is an instance of Saving or Current
                    if (account instanceof Saving savingAccount) {
                        System.out.println("CVV: " + savingAccount.getCVV());
                        System.out.println("Expiration Date: " + savingAccount.getExp_Date());
                    } else if (account instanceof Current currentAccount) {
                        System.out.println("CVV: " + currentAccount.getCVV());
                        System.out.println("Expiration Date: " + currentAccount.getExp_Date());
                    }

                    System.out.println("-----------------------------");
                }
            } else {
                System.out.println("Client not found.");
            }
        }
    }

    private Optional<Client> getClientByUsername(String username) {
        List<Client> clients = readClientsFromFile("client.txt");
        return clients.stream()
                .filter(client -> client.getUserName().equals(username))
                .findFirst();
    }


    private List<Client> readClientsFromFile(String fileName) {
        List<Client> clients = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    Client client = parseClientFromLine(line);
                    //client.initializeAccounts();  // Initialize accounts after creating the client
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

            Account account = createAccount(accountType, accountNumbers, accountBalance, user, cvv, expDate);
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

    public void Transaction_Process_Client(UserAction actionType) {
        if (actionType == UserAction.TRANSACTION_PROCESS) {
            System.out.println("Transaction Processing for " + getFirstName() + " " + getLastName());
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine();

            // Get the source account for the transaction (replace with your logic)
            Account sourceAccount = getClientAccount();

            // Create a new transaction
            Transaction transaction = new Transaction(
                    generateTransactionId(), sourceAccount, null, 0, null, getUserList());

            switch (choice) {
                case 1: // Deposit
                    transaction.setTransactionType(UserAction.DEPOSIT);
                    System.out.print("Enter the deposit amount: ");
                    int depositAmount = scanner.nextInt();
                    transaction.setAmount_Money(depositAmount);
                    transaction.setSource_Account(getSourse_Account());
                    transaction.Transaction_Process(UserAction.TRANSACTION_PROCESS);
                    transaction.saveTransactionToFile();
                    break;
                case 2: // Withdraw
                    transaction.setTransactionType(UserAction.WITHDRAWAL);
                    System.out.print("Enter the withdrawal amount: ");
                    int withdrawalAmount = scanner.nextInt();
                    transaction.setAmount_Money(withdrawalAmount);
                    transaction.Transaction_Process(UserAction.TRANSACTION_PROCESS);
                    transaction.saveTransactionToFile();
                    break;
                case 3: // Transfer
                    transaction.setTransactionType(UserAction.TRANSFER);
                    System.out.print("Enter the transfer amount: ");
                    int transferAmount = scanner.nextInt();
                    transaction.setAmount_Money(transferAmount);
                    transaction.Transaction_Process(UserAction.TRANSACTION_PROCESS);
                    transaction.saveTransactionToFile();
                    break;
                default: // Invalid choice
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                    break;
            }
        }
    }

    private Account getSourse_Account() {
        return clientAccount;
    }

    public Saving getSavingAccount() {
        // Assuming that the accountList contains a Saving account
        Optional<Account> savingAccount = accountList.stream()
                .filter(account -> account instanceof Saving)
                .findFirst();

        return (Saving) savingAccount.orElse(null); // Return the Saving account or null if not found
    }

    public Current getCurrentAccount() {
        // Assuming that the accountList contains a Current account
        Optional<Account> currentAccount = accountList.stream()
                .filter(account -> account instanceof Current)
                .findFirst();

        return (Current) currentAccount.orElse(null); // Return the Current account or null if not found
    }


    private int generateTransactionId() {
        // Concatenate the current timestamp with a random number to create a unique ID
        long timestamp = System.currentTimeMillis();
        int random = new Random().nextInt(1000); // Adjust the range as needed
        return Math.abs((int) (timestamp + random + transactionCounter++)); // Math.abd() to avoid negative values
    }


    private int generateRandomCVV() {
        // Generate a random 3-digit CVV
        Random random = new Random();
        return 100 + random.nextInt(900);
    }
    private int generateRandomAccountNumber() {
        Random random = new Random();
        int min = 100000000; // Minimum 8-digit number
        int max = 999999999; // Maximum 8-digit number
        return random.nextInt((max - min) + 1) + min;
    }
    public int[] getAccount_Number() {
        return new int[]{generateRandomAccountNumber()};
    }


    public void setAccount_Number(int[] account_Number) {
        Account_Number = account_Number;
    }

    protected int getTelephone_Number() {
        return Telephone_Number;
    }

    protected void setTelephone_Number(int telephone_Number) {
        Telephone_Number = telephone_Number;
    }

    protected String getAddress() {
        return Address;
    }

    protected void setAddress(String address) {
        Address = address;
    }

    public int getBalance() {
        return Balance;
    }

    public void setBalance(int balance) {
        Balance = balance;
    }

    public Account getClientAccount() {
        return clientAccount;
    }

    public void setClientAccount(Account clientAccount) {
        this.clientAccount = clientAccount;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public static int getTransactionCounter() {
        return transactionCounter;
    }

    public static void setTransactionCounter(int transactionCounter) {
        Client.transactionCounter = transactionCounter;
    }

    public void addAccount(Account account) {
        this.accountList.add(account);
    }

    public LocalDate getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(LocalDate lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public User getOwner() {
        return user;
    }
}