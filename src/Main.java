import enums.AccountType;
import enums.UserAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

// interfaces => to apply abstraction
// getters and setters in classes => to apply encapsulation
// constructor overloading => to apply polymorphism
// Show_TransAdmin function in User Model overridden in Admin Model => to apply polymorphism
// downcasting in authorizedUser during login => to apply polymorphism
// User model is inherited by Admin, Employee and Client models => to apply inheritance
// Account model is inherited by Saving and Current models => to apply inheritance

public class Main {

    // Create a static variable from the main class to store the current authenticated user
    private static User authenticatedUser;

    // Maximum number of login attempts if we want to change it
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public static void main(String[] args) {
        checkAndCreateFiles();

        Scanner scanner = new Scanner(System.in);

        // Login dashboard for the user
        loginPortal(scanner);

        // Depending on the user type, display the appropriate interface
        User currentUser = getCurrentUser();

        if (currentUser != null) {
            switch (currentUser) {
                case Admin admin -> adminInterface(scanner, admin);
                case Employee employee -> employeeInterface(scanner, employee);
                case Client client -> clientInterface(scanner, client);
                default -> {
                }
            }
        }

        // End of program
        System.out.println("Thank you for using the Bank Management System. Goodbye!");
    }

    private static void loginPortal(Scanner scanner) {
        System.out.println("Welcome to the Bank Management System");

        int attempts = 0;
        boolean loginSuccessful = false;

        do {
            System.out.println("1. Login as Admin");
            System.out.println("2. Login as Employee");
            System.out.println("3. Login as Client");

            int loginChoice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (loginChoice) {
                case 1:
                    // Admin login
                    int found = authenticateAdmin(scanner, "admin", "admin");

                    //downcast
                    if (found == 1) // if the admin is found
                        adminInterface(scanner, (Admin) authenticatedUser);
                    break;
                case 2:
                    // Employee login
                    found = authenticateUserFromFile(scanner, "employee.txt");
                    // polymorphism downcast
                    if (found == 1)
                        employeeInterface(scanner, (Employee) authenticatedUser);
                    break;
                case 3:
                    // Client login
                    found = authenticateUserFromFile(scanner, "client.txt");
                    // polymorphism downcast
                    if (found == 1)
                        clientInterface(scanner, (Client) authenticatedUser);
                    break;
                default:
                    System.out.println("Invalid choice.");
            }

            attempts++;

            if (!loginSuccessful && attempts < MAX_LOGIN_ATTEMPTS) {
                System.out.println("Invalid login. Please try again. Attempts left: " + (MAX_LOGIN_ATTEMPTS - attempts));
            }
        } while (!loginSuccessful && attempts < MAX_LOGIN_ATTEMPTS);

        if (!loginSuccessful) {
            System.out.println("Maximum login attempts reached. Exiting the program.");
            System.exit(1);
        }
    }

    private static int authenticateAdmin(Scanner scanner, String username, String password) {
        // Authentication for admin
        System.out.print("Enter password for admin: ");
        String adminPassword = scanner.nextLine();

        if (!password.equals(adminPassword)) {
            System.out.println("Authentication failed.");
            return 0;
        }

        authenticatedUser = new Admin(1, "Admin", "Admin", username, password);
        return 1;
    }


    private static int authenticateUserFromFile(Scanner scanner, String fileName) {
        // Authentication for employee or client
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User authenticatedUserFromFiles = authenticateFromFile(username, password, fileName);
        if (authenticatedUserFromFiles != null) {
            authenticatedUser = authenticatedUserFromFiles;
            return 1;
        } else {
            System.out.println("Authentication failed.");
            return 0;
        }
    }

    public static void checkAndCreateFiles() {
        String[] requiredFiles = {"client.txt", "employee.txt", "transaction.txt"};

        for (String fileName : requiredFiles) {
            File file = new File(fileName);

            if (!file.exists()) {
                try {
                    if (file.createNewFile()) {
                        System.out.println(fileName + " created successfully.");
                    } else {
                        System.out.println("Failed to create " + fileName);
                    }
                } catch (IOException e) {
                    System.err.println("Error creating file " + fileName + ": " + e.getMessage());
                }
            } else {
//                 System.out.println(fileName + " already exists.");
            }
        }
    }

    private static User authenticateFromFile(String username, String password, String... fileNames) {
        for (String fileName : fileNames) {
            try (Scanner fileScanner = new Scanner(new File(fileName))) {
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    String[] parts = line.split(",");

                    // Check if the parts array has enough elements
                    if (parts.length >= 5) {
                        String storedUsername = parts[3].trim();
                        String storedPassword = parts[4].trim();

                        if (username.equals(storedUsername) && password.equals(storedPassword)) {
                            // Create the corresponding User object based on the file format
                            int ID = Integer.parseInt(parts[0].trim());
                            String firstName = parts[1].trim();
                            String lastName = parts[2].trim();

                            switch (fileName) {
                                case "employee.txt":
                                    return createEmployeeFromParts(ID, firstName, lastName, username, password, parts);
                                case "client.txt":
                                    return createClientFromParts(ID, parts);
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("Error reading from file: " + e.getMessage());
            }
        }
        return null;
    }

    private static Employee createEmployeeFromParts(int ID, String firstName, String lastName, String username, String password, String[] parts) {
        String addressEmp = parts[5].trim();
        String position = parts[6].trim();
        String graduationCollege = parts[7].trim();
        int graduationYear = Integer.parseInt(parts[8].trim());
        String totalGrade = parts[9].trim();
        int salary = Integer.parseInt(parts[10].trim());

        return new Employee(ID, firstName, lastName, username, password,
                addressEmp, position, graduationCollege, graduationYear, totalGrade, salary);
    }

    private static Client createClientFromParts(int clientID, String[] parts) {
        String firstName = parts[1].trim();
        String lastName = parts[2].trim();
        String userName = parts[3].trim();
        String password = parts[4].trim();

        // Parse account numbers
        int[] accountNumbers = Arrays.stream(parts[5].trim().split("\\s+"))
                .mapToInt(Integer::parseInt)
                .toArray();

        int accountTypeIndex = 8;
        int balanceIndex = 9;
        int cvvIndex = 10;
        int expDateIndex = 11;

        List<Account> accounts = new ArrayList<>();

        // Create a User object based on the parsed information
        User user = new User(Integer.parseInt(parts[0].trim()), firstName, lastName, userName, password);


        for (int i = 0; i < accountNumbers.length; i++) {
            String accountTypeStr = parts[accountTypeIndex].trim();
            int accountBalance = Integer.parseInt(parts[balanceIndex].trim());
            int cvv = Integer.parseInt(parts[cvvIndex].trim());

            // Parse the expiration date string into a LocalDate object

            AccountType accountType = AccountType.valueOf(accountTypeStr.toUpperCase());

            String expDateString = parts[expDateIndex].trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth expDate = YearMonth.parse(expDateString, formatter);

            // Get the LocalDate by adding the last day of the month
            LocalDate expLocalDate = expDate.atEndOfMonth();


            // Create the corresponding account type based on the parsed information
            if (accountType == AccountType.SAVING) {
                accounts.add(new Saving(accountNumbers[i], accountBalance, user, cvv, expLocalDate));
            } else {
                // Assuming Current constructor requires a fee percentage, balance, and owner
                accounts.add(new Current(accountNumbers[i], accountBalance, user, cvv, expLocalDate, 3)); // Assuming 3% fee
            }


            // Increment indexes for the next account
            accountTypeIndex += 4; // Assuming each account has 4 additional pieces of information
            balanceIndex += 4;
            cvvIndex += 4;
            expDateIndex += 4;
        }

        int telephoneNumber = Integer.parseInt(parts[6].trim()); // Adjust index for telephone number
        String address = parts[7].trim(); // Adjust index for address

        // Create the client using the parsed information and the appropriate constructor
        return new Client(clientID, firstName, lastName, userName, password, accountNumbers, telephoneNumber, address, accounts);
    }


    private static void adminInterface(Scanner scanner, Admin admin) {
        String goBack;
        System.out.println("Welcome Admin! ");
        System.out.println("1. Authorize employee accounts");
        System.out.println("2. Create employee account");
        System.out.println("3. Display all employees");
        System.out.println("4. Display all clients");
        System.out.println("5. Show all transactions");
        System.out.println("6. Logout");
        System.out.println("7. Exit");

        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1:
                // Authorize new employee accounts logic
                boolean authorizeAnother = true;
                do {
                    admin.AUTH_Employee(UserAction.AUTH_EMPLOYEE);

                    // Ask if the admin wants another attempt
                    System.out.print("Do you want to authorize another employee? (y/n): ");
                    String anotherAttempt = scanner.nextLine().trim().toLowerCase();

                    if (anotherAttempt.equals("n")) {
                        // If the admin chooses not to authorize another employee, exit the program
                        authorizeAnother = false;
                    }
                } while (authorizeAnother);
                adminInterface(scanner, admin);
                break;

            case 2:
                // Create employee account logic
                boolean createAnotherEmployee = true;
                do {
                    admin.Create_Employee_Acc(UserAction.CREATE_EMPLOYEE_ACCOUNT);

                    // Ask if the admin wants to create another employee account
                    System.out.print("Do you want to create another employee account? (y/n): ");
                    String anotherAttempt = scanner.nextLine().trim().toLowerCase();

                    if (anotherAttempt.equals("n")) {
                        // If the admin chooses not to create another employee account, exit the program
                        createAnotherEmployee = false;
                    }
                } while (createAnotherEmployee);
                adminInterface(scanner, admin);
                break;
            case 3:
                // Display all employees logic
                admin.Display_Employee_info(UserAction.DISPLAY_EMPLOYEE_INFO);
                System.out.print("Do you want to go back to the admin interface? (y/n): ");
                goBack = scanner.nextLine().trim().toLowerCase();

                if (goBack.equals("n")) {
                    // If the admin chooses not to go back, exit the program
                    System.exit(0);
                }
                adminInterface(scanner, admin);
                break;
            case 4:
                // Display all clients logic
                admin.Display_Client_info(UserAction.DISPLAY_CLIENT_INFO);
                System.out.print("Do you want to go back to the admin interface? (y/n): ");
                goBack = scanner.nextLine().trim().toLowerCase();

                if (goBack.equals("n")) {
                    // If the admin chooses not to go back, exit the program
                    System.exit(0);
                }
                adminInterface(scanner, admin);
                break;
            case 5:
                // Show all transactions logic
                admin.Show_TransAdmin(UserAction.SHOW_TRANSACTIONS);
                System.out.print("Do you want to go back to the admin interface? (y/n): ");
                goBack = scanner.nextLine().trim().toLowerCase();

                if (goBack.equals("n")) {
                    // If the admin chooses not to go back, exit the program
                    System.exit(0);
                }
                adminInterface(scanner, admin);
                break;
            case 6:
                // Logout logic
                System.out.println("Logging out. Returning to the login portal.");
                // as deleting the authenticated user session and then to create another session for another user
                authenticatedUser = null;
                loginPortal(scanner);
                break;
            case 7:
                // Exit program
                System.out.println("Exiting the program. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void employeeInterface(Scanner scanner, Employee employee) {
        // Implement the employee interface
        String goBack;
        System.out.println("Employee Menu - Welcome " + employee.getFirstName());
        System.out.println("1. Edit personal information");
        System.out.println("2. Create a client account");
        System.out.println("3. Edit a client account");
        System.out.println("4. Search for a client");
        System.out.println("5. Delete a client account");
        System.out.println("6. Logout");
        System.out.println("7. Exit");

        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1:
                // Edit personal information logic
                employee.editPersonalInfo(UserAction.EDIT_PERSONAL_INFO);
                System.out.print("Do you want to go back to the employee menu? (y/n): ");
                goBack = scanner.nextLine().trim().toLowerCase();
                if (!goBack.equals("y")) {
                    // If the admin chooses not to go back, exit the loop
                    System.exit(0);
                }
                employeeInterface(scanner, employee);
                break;
            case 2:
                // Create a client account logic
                boolean createAnother = true;
                // will pass the .CREATE value from the userAction enum for readability of the function inside
                employee.Create_Client_Acc(UserAction.CREATE);

                // Ask if the admin wants another attempt

                employeeInterface(scanner, employee);

                break;
            case 3:
                // Edit a client account logic
                createAnother = true;
                do {
                    employee.Edit_Client_Acc(UserAction.EDIT);

                    // Ask if the admin wants another attempt
                    System.out.print("Do you want to Edit another client? (y/n): ");
                    String anotherAttempt = scanner.nextLine().trim().toLowerCase();

                    if (!anotherAttempt.equals("y")) {
                        createAnother = false;
                    }
                } while (createAnother);
                employeeInterface(scanner, employee);
                break;
            case 4:
                // Search for a client logic
                createAnother = true;
                do {
                    employee.Search_Client_Acc(UserAction.SEARCH);

                    // Ask if the admin wants another attempt
                    System.out.print("Do you want to Search another client? (y/n): ");
                    String anotherAttempt = scanner.nextLine().trim().toLowerCase();

                    if (!anotherAttempt.equals("y")) {
                        createAnother = false;
                    }
                } while (createAnother);
                employeeInterface(scanner, employee);
                break;
            case 5:
                // Delete a client account logic
                createAnother = true;
                do {
                    employee.Delete_Client_Acc(UserAction.DELETE);

                    // Ask if the admin wants another attempt
                    System.out.print("Do you want to Delete another client? (y/n): ");
                    String anotherAttempt = scanner.nextLine().trim().toLowerCase();

                    if (!anotherAttempt.equals("y")) {
                        createAnother = false;
                    }
                } while (createAnother);
                employeeInterface(scanner, employee);
                break;
            case 6:
                // Logout logic
                System.out.println("Logging out. Returning to the login portal.");
                authenticatedUser = null;
                loginPortal(scanner);
                break;

            case 7:
                // Exit program
                System.out.println("Exiting the program. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void clientInterface(Scanner scanner, Client client) {

        System.out.println("Client Interface - Welcome " + client.getFirstName());
        System.out.println("1. Edit personal information");
        System.out.println("2. Display account details");
        System.out.println("3. Transaction processing");
        System.out.println("4. Show Transactions");
        System.out.println("5. Logout");
        System.out.println("6. Exit");

        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1:
                // Edit personal information logic
                client.Edit_Pers_info(UserAction.EDIT_PERSONAL_INFO);
                clientInterface(scanner, client);
                break;
            case 2:
                // Display account details logic
                client.Display_Acc_Details(UserAction.DISPLAY_DETAILS);
                System.out.println("Do you want to go back to the client interface? (y/n): ");
                String goBack = scanner.nextLine().trim().toLowerCase();
                if (!goBack.equals("y"))
                    System.exit(0);
                clientInterface(scanner, client);
                break;
            case 3:
                // Transaction processing logic
                client.Transaction_Process_Client(UserAction.TRANSACTION_PROCESS);

                System.out.println("Do you want to go back to the client interface? (y/n): ");
                if (scanner.hasNextLine()) { // Check if there is a next line
                    goBack = scanner.nextLine().trim().toLowerCase();
                    if (!goBack.equals("y"))
                        System.exit(0);
                    clientInterface(scanner, client);
                } else {
                    System.out.println("No input found. Exiting the program.");
                    System.exit(0);
                }
                break;


            case 4:
                // Show Transactions logic
                client.Show_Trans(UserAction.SHOW_TRANSACTIONS);
                System.out.println("Do you want to go back to the client interface? (y/n): ");
                goBack = scanner.nextLine().trim().toLowerCase();
                if (!goBack.equals("y"))
                    System.exit(0);
                clientInterface(scanner, client);
                break;
            case 5:
                // Logout logic
                System.out.println("Logging out. Returning to the login portal.");
                authenticatedUser = null;
                loginPortal(scanner);
                break;
            case 6:
                // Exit program
                System.out.println("Exiting the program. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static User getCurrentUser() {
        // Return the current authenticated user
        return authenticatedUser;
    }
}