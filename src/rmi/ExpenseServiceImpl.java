package rmi;

import model.Expense;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ExpenseService RMI interface (Week 10)
 * 
 * Key concepts demonstrated:
 * - Extends UnicastRemoteObject for RMI functionality
 * - Stores expenses in memory (in real app, would use database)
 * - All methods throw RemoteException (required for RMI)
 * - Expense objects are serialized when passed between client and server
 * - Demonstrates parameter passing by value (not by reference)
 */
public class ExpenseServiceImpl extends UnicastRemoteObject implements ExpenseService {
    
    // In-memory storage for expenses (in production, use database)
    private final List<Expense> expenses;
    
    /**
     * Constructor - must throw RemoteException
     */
    public ExpenseServiceImpl() throws RemoteException {
        super(); // Calls UnicastRemoteObject constructor
        this.expenses = new ArrayList<>();
        System.out.println("[ExpenseService] Service implementation created");
    }
    
    @Override
    public String addExpense(Expense expense) throws RemoteException {
        // Expense object is received after serialization/deserialization
        System.out.println("[ExpenseService] Received expense: " + expense);
        expenses.add(expense);
        System.out.println("[ExpenseService] Total expenses: " + expenses.size());
        return "Expense added successfully: " + expense.getName() + " ($" + expense.getAmount() + ")";
    }
    
    @Override
    public List<Expense> getAllExpenses() throws RemoteException {
        System.out.println("[ExpenseService] Returning " + expenses.size() + " expenses");
        // List is serialized when sent back to client
        return new ArrayList<>(expenses); // Return copy to demonstrate serialization
    }
    
    @Override
    public List<Expense> getExpensesByCategory(String category) throws RemoteException {
        System.out.println("[ExpenseService] Filtering expenses by category: " + category);
        List<Expense> filtered = expenses.stream()
                .filter(e -> e.getCategory() != null && e.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
        return filtered;
    }
    
    @Override
    public double getTotalAmount() throws RemoteException {
        double total = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
        System.out.println("[ExpenseService] Total amount calculated: $" + total);
        return total;
    }
    
    @Override
    public int getExpenseCount() throws RemoteException {
        return expenses.size();
    }
    
    @Override
    public String concatenateStrings(String str1, String str2) throws RemoteException {
        System.out.println("[ExpenseService] Concatenating: '" + str1 + "' + '" + str2 + "'");
        // Demonstrates simple parameter passing by value
        return str1 + str2;
    }
}

