import enums.UserAction;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

class Admin extends User {
    private User user;

    public Admin(int ID, String FirstName, String LastName, String UserName, String Password) {
        super(ID, FirstName, LastName, "Admin", "Admin");
    }

    public void Display_Employee_info(UserAction actionType) {
        // readability and seconed layer validation to confirm the action type is DISPLAY_EMPLOYEE_INFO
        if (actionType == UserAction.DISPLAY_EMPLOYEE_INFO) {
            // Read employees from the file function returns list of employees
            List<Employee> employees = readEmployeesFromFile("employee.txt");
            for (Employee employee : employees) {
                System.out.println("Employee ID: " + employee.getID());
                System.out.println();
                System.out.println("Employee Name: " + employee.getFirstName() + " " + employee.getLastName());
                System.out.println("Employee Username: " + employee.getUserName());
                System.out.println("Employee Password: " + employee.getPassword());
                System.out.println("Employee Position: " + employee.getPosition());
                System.out.println("Employee Address: " + employee.getAddressEmp());
                System.out.println("Employee Graduation College: " + employee.getGraduation_College());
                System.out.println("Employee Graduation Year: " + employee.getGraduation_Year());
                System.out.println("Employee Total Grade: " + employee.getTotal_Grade());
                System.out.println("Employee Salary: " + employee.getSalary());
                System.out.println();
                System.out.println("--------------------------------------------------");
                System.out.println();
            }
        }
    }

    public void AUTH_Employee(UserAction actionType) {
        //The if condition of UserAction.AUTH_EMPLOYEE is for readability and seconed layer validation to confirm the action type is AUTH_EMPLOYEE
        if (actionType == UserAction.AUTH_EMPLOYEE) {
            try {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter employee username: ");
                String username = scanner.nextLine();
                System.out.print("Enter employee password: ");
                String password = scanner.nextLine();

                List<Employee> employees = readEmployeesFromFile("employee.txt");
                for (Employee employee : employees) {
                    if (employee.getUserName().equals(username) && employee.getPassword().equals(password)) {
                        System.out.println("Authentication successful.");
                        return;
                    }
                }

                System.out.println("Authentication failed. Employee not found.");
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please try again.");
            }
        }
    }

    public void Create_Employee_Acc(UserAction actionType) {
        // readability and seconed layer validation and to confirm the action type is CREATE_EMPLOYEE_ACCOUNT
        if (actionType == UserAction.CREATE_EMPLOYEE_ACCOUNT) {
            try {
                Scanner scanner = new Scanner(System.in);
                int id = generateRandomEmployeeId();
                scanner.nextLine();
                System.out.print("Enter employee first name: ");
                String firstName = scanner.nextLine();
                System.out.print("Enter employee last name: ");
                String lastName = scanner.nextLine();
                System.out.print("Enter employee username: ");
                String username = scanner.nextLine();
                System.out.print("Enter employee password: ");
                String password = scanner.nextLine();
                System.out.print("Enter employee position: ");
                String position = scanner.nextLine();
                System.out.print("Enter employee address: ");
                String addressEmp = scanner.nextLine();
                System.out.print("Enter employee graduation college: ");
                String graduation_College = scanner.nextLine();
                System.out.print("Enter employee graduation year: ");
                int graduation_Year = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter employee total grade: ");
                String total_Grade = scanner.nextLine();
                System.out.print("Enter employee salary: ");
                int salary = Integer.parseInt(scanner.nextLine());

                // Create a new Employee object
                Employee newEmployee = new Employee(id, firstName, lastName, username, password, addressEmp, position, graduation_College, graduation_Year, total_Grade, salary);

                // Save the new employee to the file
                saveEmployeeToFile("employee.txt", newEmployee);

                System.out.println("Employee account created successfully.");

            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please try again.");
            }
        }
    }

    private int generateRandomEmployeeId() {
        // Generate a random 5-digit employee ID
        /*
        To ensure that the generated ID is always a 5-digit number, 10000 is added to the randomly generated number.
        This means that the smallest possible ID that can be generated is 10000 + 0 = 10000 and the largest possible ID is 10000 + 89999 = 99999.
         */
        return 10000 + new Random().nextInt(90000);
    }

    private List<Employee> readEmployeesFromFile(String fileName) {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse employee data from the line and create Employee objects
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0].trim());
                String firstName = parts[1].trim();
                String lastName = parts[2].trim();
                String username = parts[3].trim();
                String password = parts[4].trim();
                String addressEmp = parts[5].trim();
                String position = parts[6].trim();
                String graduation_College = parts[7].trim();
                int graduation_Year = Integer.parseInt(parts[8].trim());
                String total_Grade = parts[9].trim();
                int salary = Integer.parseInt(parts[10].trim());

                Employee employee = new Employee(id, firstName, lastName, username, password, addressEmp, position, graduation_College, graduation_Year, total_Grade, salary);
                employees.add(employee);
            }
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
        return employees;
    }

    private void saveEmployeeToFile(String fileName, Employee employee) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            // Write employee data to the file
            writer.write(employee.getID() + "," +
                    employee.getFirstName() + "," +
                    employee.getLastName() + "," +
                    employee.getUserName() + "," +
                    employee.getPassword() + "," +
                    employee.getAddressEmp() + "," +
                    employee.getPosition() + "," +
                    employee.getGraduation_College() + "," +
                    employee.getGraduation_Year() + "," +
                    employee.getTotal_Grade() + "," +
                    employee.getSalary());

            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public void Display_Client_info(UserAction userAction) {
        // Implementation for displaying client information
        // readability and second layer validation to confirm the action type is DISPLAY_CLIENT_INFO
        if (userAction == UserAction.DISPLAY_CLIENT_INFO) {
            List<Client> clients = readClientsFromFile("client.txt");

            if (clients.isEmpty()) {
                System.out.println("No clients found.");
                return;
            }

            for (Client client : clients) {
                System.out.println("Client ID: " + client.getID());
                System.out.println("Client Name: " + client.getFirstName() + " " + client.getLastName());
                System.out.println("Client Username: " + client.getUserName());

                List<Account> clientAccounts = client.getAccountList();
                if (!clientAccounts.isEmpty()) {
                    System.out.println("Client Accounts:");

                    for (Account clientAccount : clientAccounts) {
                        System.out.println("Account Number: " + clientAccount.getAccount_Number());
                        System.out.println("Balance: " + clientAccount.getBalance());
                        System.out.println("CVV: " + clientAccount.getCVV());
                        System.out.println("Expiration Date: " + clientAccount.getExp_Date());
                        System.out.println("Account State: " + (clientAccount.isAccount_State() ? "Active" : "Closed"));
                        System.out.println("Account Type: " + clientAccount.getClass().getSimpleName());
                        System.out.println("-------------------------------");
                    }
                } else {
                    System.out.println("No account information found for the client.");
                }

                System.out.println("Client Telephone Number: " + client.getTelephone_Number());
                System.out.println("Client Address: " + client.getAddress());
                System.out.println();
                System.out.println("--------------------------------------------------");
                System.out.println();
            }
        }
    }


    public void Show_TransAdmin(UserAction type) {
        if (type == UserAction.SHOW_TRANSACTIONS) {
            // Read transactions from the file
            List<String> transactions = readTransactionsFromFile();

            // Display transactions
            if (!transactions.isEmpty()) {
                System.out.println("All Transactions:");
                for (String transaction : transactions) {
                    // Display each transaction
                    System.out.println(transaction);
                }
            } else {
                System.out.println("No transactions found.");
            }
        }
    }

    private List<Client> readClientsFromFile(String fileName) {
        List<Client> clients = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    Client client = parseClientFromLine(line);
                    clients.add(client);
                } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    System.err.println("Error parsing data at line: " + line + ". Skipping the invalid entry.");
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

}