package rmi;

import model.Expense;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for ExpenseService RMI (Week 10)
 * Demonstrates passing custom objects (Expense) over network via RMI
 * 
 * Key concepts:
 * - All methods must throw RemoteException
 * - Expense objects are passed by value (serialized)
 * - Interface extends Remote to indicate it's a remote interface
 */
public interface ExpenseService extends Remote {
    
    /**
     * Add an expense to the remote server
     * @param expense Expense object (will be serialized and sent over network)
     * @return confirmation message
     * @throws RemoteException if network communication fails
     */
    String addExpense(Expense expense) throws RemoteException;
    
    /**
     * Get all expenses from the remote server
     * @return List of Expense objects (serialized and sent back)
     * @throws RemoteException if network communication fails
     */
    List<Expense> getAllExpenses() throws RemoteException;
    
    /**
     * Get expenses by category
     * @param category category to filter by
     * @return List of Expense objects matching the category
     * @throws RemoteException if network communication fails
     */
    List<Expense> getExpensesByCategory(String category) throws RemoteException;
    
    /**
     * Calculate total amount of all expenses
     * @return total amount as double
     * @throws RemoteException if network communication fails
     */
    double getTotalAmount() throws RemoteException;
    
    /**
     * Get expense count
     * @return number of expenses stored
     * @throws RemoteException if network communication fails
     */
    int getExpenseCount() throws RemoteException;
    
    /**
     * Test method: concatenate strings (demonstrates parameter passing)
     * @param str1 first string
     * @param str2 second string
     * @return concatenated string
     * @throws RemoteException if network communication fails
     */
    String concatenateStrings(String str1, String str2) throws RemoteException;
}

