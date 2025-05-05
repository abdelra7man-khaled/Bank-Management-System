import enums.UserAction;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class User {
    private int ID;
    public String FirstName;
    public String LastName;
    public String UserName;
    private String Password;
    protected Account account;
    private List<Account> accounts;


    public User(int ID, String firstName, String lastName, String userName, String password) {
        this.ID = ID;
        this.FirstName = firstName;
        this.LastName = lastName;
        this.UserName = userName;
        this.Password = password;
        this.accounts = new ArrayList<>();
    }


    public void Edit_Pers_info(UserAction type) {
        if (type == UserAction.EDIT_PERSONAL_INFO) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                System.out.println("Select the information to update:");
                System.out.println("1. First Name");
                System.out.println("2. Last Name");
                System.out.println("3. Username");
                System.out.println("4. Password");

                int choice = Integer.parseInt(reader.readLine());

                switch (choice) {
                    case 1:
                        System.out.print("Enter new first name: ");
                        setFirstName(reader.readLine());
                        break;
                    case 2:
                        System.out.print("Enter new last name: ");
                        setLastName(reader.readLine());
                        break;
                    case 3:
                        System.out.print("Enter new username: ");
                        setUserName(reader.readLine());
                        break;
                    case 4:
                        System.out.print("Enter new password: ");
                        setPassword(reader.readLine());
                        break;
                    default:
                        System.out.println("Invalid choice.");
                        return;
                }

                // Save updated personal information to the file
                updatePersonalInfoInFile();
                System.out.println("Personal information updated successfully.");
            } catch (IOException | NumberFormatException e) {
                System.err.println("Error reading from input: " + e.getMessage());
            }
        }
    }

    public void Show_Trans(UserAction type) {
        if (type == UserAction.SHOW_TRANSACTIONS) {
            // Read transactions from the file
            List<String> transactions = readTransactionsFromFile();

            // Display transactions for the user's account
            if (!transactions.isEmpty()) {
                String userUserName = getUserName(); // getUserName() returns String
                System.out.println("Transactions for User: " + userUserName);

                for (String transaction : transactions) {
                    // Check if the transaction contains "Owner" and "Transferred to" keys
                    if (transaction.contains("Owner") || transaction.contains("Transferred to")) {
                        String[] keyValuePairs = transaction.split(", ");
                        String transactionOwner = null;
                        String transferredTo = null;

                        for (String pair : keyValuePairs) {
                            String[] entry = pair.split(": ");
                            if (entry.length == 2 && entry[0].trim().equals("Owner")) {
                                transactionOwner = entry[1].trim();
                            } else if (entry.length == 2 && entry[0].trim().equals("Transferred to")) {
                                transferredTo = entry[1].trim();
                            }
                        }

                        // Check if the transaction owner's username or transferredTo matches the user's username
                        if ((transactionOwner != null && userUserName.equals(transactionOwner)) ||
                                (transferredTo != null && userUserName.equals(transferredTo))) {
                            // Display the matching transaction
                            System.out.println(transaction);
                        }
                    } else {
                        System.out.println("Error: Transaction data does not contain 'Owner' and 'Transferred to' information: " + transaction);
                    }
                }
            } else {
                System.out.println("No transactions found.");
            }
        }
    }


    // this method is for the admin to see all the transactions so it is to be in admin class to make overriding in polymorphism
    // it is overridden in the admin class
    public void Show_TransAdmin(UserAction type) {

    }



    public List<String> readTransactionsFromFile() {
        List<String> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("transaction.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                transactions.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading from transaction file: " + e.getMessage());
        }
        return transactions;
    }

    private void updatePersonalInfoInFile() {
        // Read existing content
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("client.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading client file: " + e.getMessage());
            return;
        }

        // Find and update the line corresponding to the client
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            int clientId = Integer.parseInt(parts[0].trim());

            if (clientId == getID()) {
                // Update the necessary fields (e.g., first name, last name, username, password)
                parts[1] = getFirstName();
                parts[2] = getLastName();
                parts[3] = getUserName();
                parts[4] = getPassword();

                // Join the parts back into a line
                lines.set(i, String.join(",", parts));

                // No need to continue searching
                break;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("client.txt"))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to client file: " + e.getMessage());
        }
    }

    // getters and setters
    public int getID() {
        return ID;
    }
    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }


    public List<Account> getAccounts() {
        return accounts;
    }


}