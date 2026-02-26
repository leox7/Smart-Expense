package rmi;

import model.Expense;
import java.rmi.Naming;
import java.time.LocalDate;
import java.util.List;

/**
 * ExpenseClient - RMI Client with Network Connection Support (Week 10)
 * 
 * Features:
 * - Connects to remote RMI server using IP address
 * - Demonstrates object serialization (Expense objects passed by value)
 * - Shows parameter passing and return values
 * - Supports both localhost and network connections
 * 
 * Usage:
 *   Local:  java rmi.ExpenseClient
 *   Remote: java rmi.ExpenseClient 192.168.1.100 1099
 * 
 * Network Connection Requirements:
 * 1. Server must be running on target IP
 * 2. Firewall must allow connection on port 1099
 * 3. Both client and server need Expense class in classpath
 * 4. Server's RMI registry must be accessible from client's network
 */
public class ExpenseClient {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1099;
    private static final String SERVICE_NAME = "ExpenseService";
    
    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        
        // Parse command-line arguments for remote connection
        if (args.length >= 1) {
            host = args[0];
            System.out.println("[ExpenseClient] Connecting to host: " + host);
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("[ExpenseClient] Invalid port, using default: " + DEFAULT_PORT);
            }
        }
        
        try {
            // Construct service URL
            String serviceUrl = "rmi://" + host + ":" + port + "/" + SERVICE_NAME;
            System.out.println("[ExpenseClient] Looking up service at: " + serviceUrl);
            
            // Look up remote service
            ExpenseService expenseService = (ExpenseService) Naming.lookup(serviceUrl);
            System.out.println("[ExpenseClient] Successfully connected to ExpenseService!");
            System.out.println();
            
            // Demonstrate string concatenation (parameter passing by value)
            System.out.println("=== Demonstration 1: String Concatenation ===");
            String result = expenseService.concatenateStrings("Hello", " World");
            System.out.println("[ExpenseClient] Received: " + result);
            System.out.println();
            
            // Demonstrate adding Expense objects (object serialization)
            System.out.println("=== Demonstration 2: Adding Expense Objects ===");
            Expense expense1 = new Expense("Coffee", 4.50, "Food", LocalDate.now());
            Expense expense2 = new Expense("Gas", 45.00, "Transportation", LocalDate.now());
            Expense expense3 = new Expense("Movie", 12.00, "Entertainment", LocalDate.now());
            
            System.out.println("[ExpenseClient] Adding expense 1: " + expense1);
            String response1 = expenseService.addExpense(expense1);
            System.out.println("[ExpenseClient] Server response: " + response1);
            System.out.println();
            
            System.out.println("[ExpenseClient] Adding expense 2: " + expense2);
            String response2 = expenseService.addExpense(expense2);
            System.out.println("[ExpenseClient] Server response: " + response2);
            System.out.println();
            
            System.out.println("[ExpenseClient] Adding expense 3: " + expense3);
            String response3 = expenseService.addExpense(expense3);
            System.out.println("[ExpenseClient] Server response: " + response3);
            System.out.println();
            
            // Demonstrate retrieving all expenses (object serialization in return)
            System.out.println("=== Demonstration 3: Retrieving All Expenses ===");
            List<Expense> allExpenses = expenseService.getAllExpenses();
            System.out.println("[ExpenseClient] Received " + allExpenses.size() + " expenses:");
            for (Expense exp : allExpenses) {
                System.out.println("  - " + exp);
            }
            System.out.println();
            
            // Demonstrate filtering by category
            System.out.println("=== Demonstration 4: Filtering by Category ===");
            List<Expense> foodExpenses = expenseService.getExpensesByCategory("Food");
            System.out.println("[ExpenseClient] Food expenses: " + foodExpenses.size());
            for (Expense exp : foodExpenses) {
                System.out.println("  - " + exp);
            }
            System.out.println();
            
            // Demonstrate calculating total
            System.out.println("=== Demonstration 5: Calculating Total ===");
            double total = expenseService.getTotalAmount();
            System.out.println("[ExpenseClient] Total amount: $" + total);
            System.out.println();
            
            // Demonstrate getting count
            System.out.println("=== Demonstration 6: Getting Count ===");
            int count = expenseService.getExpenseCount();
            System.out.println("[ExpenseClient] Total expenses: " + count);
            System.out.println();
            
            System.out.println("[ExpenseClient] All demonstrations completed successfully!");
            
        } catch (Exception e) {
            System.err.println("[ExpenseClient] Error connecting to server: " + e.getMessage());
            e.printStackTrace();
            System.err.println("\n[ExpenseClient] Troubleshooting:");
            System.err.println("1. Ensure server is running: java rmi.ExpenseServer [host] [port]");
            System.err.println("2. Check firewall settings on server");
            System.err.println("3. Verify host and port are correct");
            System.err.println("4. Ensure both machines can reach each other on network");
            System.err.println("5. Check that Expense class is in classpath on both client and server");
            System.exit(1);
        }
    }
}

