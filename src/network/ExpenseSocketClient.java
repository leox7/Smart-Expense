package network;

import model.Expense;
import network.ExpenseSocketServer.Command;
import network.ExpenseSocketServer.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * ExpenseSocketClient - Socket Client for Expense Operations (Week 11)
 * 
 * Demonstrates:
 * - Client socket connection to expense server
 * - Sending Expense objects via ObjectOutputStream
 * - Receiving Expense objects via ObjectInputStream
 * - Command-based protocol using serialized Command objects
 * 
 * Usage:
 *   java network.ExpenseSocketClient [host] [port]
 *   Default host: localhost
 *   Default port: 9999
 * 
 * Commands:
 *   ADD - Add an expense (will prompt for details)
 *   GET_ALL - Get all expenses
 *   GET_BY_CATEGORY - Get expenses by category (will prompt for category)
 *   GET_TOTAL - Get total amount
 *   COUNT - Get expense count
 *   BYE - Disconnect
 */
public class ExpenseSocketClient {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9999;
    
    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port, using default: " + DEFAULT_PORT);
            }
        }
        
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {
            
            // Read connection acknowledgment
            Response welcome = (Response) objectIn.readObject();
            System.out.println("[Server] " + welcome.message);
            System.out.println("[ExpenseSocketClient] Connected to server: " + host + ":" + port);
            System.out.println("[ExpenseSocketClient] Available commands:");
            System.out.println("  ADD - Add an expense");
            System.out.println("  GET_ALL - Get all expenses");
            System.out.println("  GET_BY_CATEGORY - Get expenses by category");
            System.out.println("  GET_TOTAL - Get total amount");
            System.out.println("  COUNT - Get expense count");
            System.out.println("  BYE - Disconnect");
            System.out.println();
            
            while (true) {
                System.out.print("[Command] ");
                String command = scanner.nextLine().trim().toUpperCase();
                
                if (command.isEmpty()) {
                    continue;
                }
                
                Command cmd = null;
                
                // Create appropriate command object
                switch (command) {
                    case "ADD":
                        // Prompt for expense details
                        System.out.print("Expense name: ");
                        String name = scanner.nextLine();
                        System.out.print("Amount: ");
                        double amount = Double.parseDouble(scanner.nextLine());
                        System.out.print("Category: ");
                        String category = scanner.nextLine();
                        System.out.print("Date (YYYY-MM-DD): ");
                        LocalDate date = LocalDate.parse(scanner.nextLine());
                        
                        Expense expense = new Expense(name, amount, category, date);
                        cmd = new Command("ADD", expense);
                        break;
                        
                    case "GET_ALL":
                        cmd = new Command("GET_ALL");
                        break;
                        
                    case "GET_BY_CATEGORY":
                        System.out.print("Category: ");
                        String cat = scanner.nextLine();
                        cmd = new Command("GET_BY_CATEGORY", cat);
                        break;
                        
                    case "GET_TOTAL":
                        cmd = new Command("GET_TOTAL");
                        break;
                        
                    case "COUNT":
                        cmd = new Command("COUNT");
                        break;
                        
                    case "BYE":
                        cmd = new Command("BYE");
                        break;
                        
                    default:
                        System.out.println("Unknown command: " + command);
                        System.out.println();
                        continue;
                }
                
                // Send command to server
                objectOut.writeObject(cmd);
                objectOut.flush();
                
                // Read response from server
                Response response = (Response) objectIn.readObject();
                
                if ("ERROR".equals(response.status)) {
                    System.out.println("[Server] ERROR: " + response.message);
                } else if ("SUCCESS".equals(response.status)) {
                    // Handle different response types
                    if (response.data instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Expense> expenses = (List<Expense>) response.data;
                        System.out.println("[Server] Received " + expenses.size() + " expenses:");
                        for (Expense exp : expenses) {
                            System.out.println("  - " + exp);
                        }
                    } else if (response.data instanceof Double) {
                        System.out.println("[Server] Total amount: $" + response.data);
                    } else if (response.data instanceof Integer) {
                        System.out.println("[Server] Expense count: " + response.data);
                    } else if (response.message != null) {
                        System.out.println("[Server] " + response.message);
                    } else {
                        System.out.println("[Server] SUCCESS");
                    }
                    
                    // Exit if BYE command
                    if ("BYE".equals(command)) {
                        System.out.println("[ExpenseSocketClient] Disconnecting...");
                        break;
                    }
                }
                
                System.out.println();
            }
            
        } catch (IOException e) {
            System.err.println("[ExpenseSocketClient] Error: " + e.getMessage());
            System.err.println("\nTroubleshooting:");
            System.err.println("1. Ensure server is running: java network.ExpenseSocketServer [port]");
            System.err.println("2. Check host and port are correct");
            System.err.println("3. Verify firewall allows connection");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("[ExpenseSocketClient] Error reading response: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
