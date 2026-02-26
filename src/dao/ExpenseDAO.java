package dao;

import model.Expense;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Expense operations using JDBC
 * Demonstrates PreparedStatement, ResultSet, and ResultSetMetaData
 */
public class ExpenseDAO {
    
    /**
     * Add expense to database using PreparedStatement
     * Demonstrates SQL injection prevention and parameter binding
     */
    public boolean addExpense(Expense expense, int userId) {
        String sql = "INSERT INTO expenses (user_id, name, amount, category, expense_date) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Set parameters using PreparedStatement (prevents SQL injection)
            pstmt.setInt(1, userId);
            pstmt.setString(2, expense.getName());
            pstmt.setDouble(3, expense.getAmount());
            pstmt.setString(4, expense.getCategory());
            pstmt.setDate(5, Date.valueOf(expense.getDate()));
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get generated key (expense_id)
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    System.out.println("Expense added with ID: " + generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding expense: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get all expenses for a user with ResultSet and ResultSetMetaData demonstration
     * Shows how to process result sets and analyze table structure
     */
    public List<Expense> getExpensesByUser(int userId) {
        String sql = "SELECT * FROM expenses WHERE user_id = ? ORDER BY expense_date DESC";
        List<Expense> expenses = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            // Demonstrate ResultSetMetaData (Week 6 topic)
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            System.out.println("\n=== Table Structure Analysis ===");
            System.out.println("Table: " + metaData.getTableName(1));
            System.out.println("Columns:");
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("  %d. %s (%s) - %s%n", 
                    i, 
                    metaData.getColumnName(i), 
                    metaData.getColumnTypeName(i),
                    metaData.isNullable(i) == ResultSetMetaData.columnNullable ? "NULL" : "NOT NULL");
            }
            System.out.println("===============================\n");
            
            // Process ResultSet (cursor traversal)
            while (rs.next()) {
                Expense expense = new Expense(
                    rs.getString("name"),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    rs.getDate("expense_date").toLocalDate()
                );
                expenses.add(expense);
            }
            
            System.out.println("Retrieved " + expenses.size() + " expenses from database");
            
        } catch (SQLException e) {
            System.err.println("Error retrieving expenses: " + e.getMessage());
            e.printStackTrace();
        }
        return expenses;
    }
    
    /**
     * Update existing expense using PreparedStatement
     * Demonstrates UPDATE operations with parameter binding
     */
    public boolean updateExpense(int expenseId, Expense updatedExpense) {
        String sql = "UPDATE expenses SET name = ?, amount = ?, category = ?, expense_date = ? WHERE expense_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, updatedExpense.getName());
            pstmt.setDouble(2, updatedExpense.getAmount());
            pstmt.setString(3, updatedExpense.getCategory());
            pstmt.setDate(4, Date.valueOf(updatedExpense.getDate()));
            pstmt.setInt(5, expenseId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating expense: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete expense from database
     */
    public boolean deleteExpense(int expenseId) {
        String sql = "DELETE FROM expenses WHERE expense_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, expenseId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting expense: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get expense statistics for a user
     * Demonstrates aggregate functions and formatted output
     */
    public void printExpenseStatistics(int userId) {
        String sql = "SELECT " +
                    "COUNT(*) as total_count, " +
                    "SUM(amount) as total_amount, " +
                    "AVG(amount) as average_amount, " +
                    "MIN(amount) as min_amount, " +
                    "MAX(amount) as max_amount " +
                    "FROM expenses WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("\n=== Expense Statistics ===");
                System.out.printf("Total Expenses: %d%n", rs.getInt("total_count"));
                System.out.printf("Total Amount: $%.2f%n", rs.getDouble("total_amount"));
                System.out.printf("Average Amount: $%.2f%n", rs.getDouble("average_amount"));
                System.out.printf("Min Amount: $%.2f%n", rs.getDouble("min_amount"));
                System.out.printf("Max Amount: $%.2f%n", rs.getDouble("max_amount"));
                System.out.println("==========================\n");
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
