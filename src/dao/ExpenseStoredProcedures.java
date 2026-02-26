package dao;

import model.Expense;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates stored procedures using CallableStatement (Week 6 topic)
 * Shows how to call database stored procedures from Java
 */
public class ExpenseStoredProcedures {
    
    /**
     * Call stored procedure to get expenses by user and category
     * Demonstrates CallableStatement with input parameters
     */
    public List<Expense> getExpensesByUserAndCategory(int userId, String category) {
        String sql = "{CALL GetExpensesByUserAndCategory(?, ?)}";
        List<Expense> expenses = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            // Set input parameters
            cstmt.setInt(1, userId);
            cstmt.setString(2, category);
            
            // Execute the stored procedure
            ResultSet rs = cstmt.executeQuery();
            
            System.out.println("Calling stored procedure: GetExpensesByUserAndCategory");
            System.out.println("Parameters: userId=" + userId + ", category=" + category);
            
            while (rs.next()) {
                Expense expense = new Expense(
                    rs.getString("name"),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    rs.getDate("expense_date").toLocalDate()
                );
                expenses.add(expense);
            }
            
            System.out.println("Stored procedure returned " + expenses.size() + " expenses");
            
        } catch (SQLException e) {
            System.err.println("Error calling stored procedure: " + e.getMessage());
            e.printStackTrace();
        }
        return expenses;
    }
    
    /**
     * Call stored procedure to get expense summary by category
     * Demonstrates stored procedure with ResultSet processing
     */
    public void printExpenseSummary(int userId) {
        String sql = "{CALL GetExpenseSummary(?)}";
        
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, userId);
            ResultSet rs = cstmt.executeQuery();
            
            System.out.println("\n=== Expense Summary (Stored Procedure) ===");
            System.out.println("Category\t\tCount\tTotal");
            System.out.println("----------------------------------------");
            
            double grandTotal = 0;
            int totalCount = 0;
            
            while (rs.next()) {
                String category = rs.getString("category");
                int count = rs.getInt("count");
                double total = rs.getDouble("total");
                
                System.out.printf("%-20s\t%d\t$%.2f%n", category, count, total);
                grandTotal += total;
                totalCount += count;
            }
            
            System.out.println("----------------------------------------");
            System.out.printf("%-20s\t%d\t$%.2f%n", "GRAND TOTAL", totalCount, grandTotal);
            System.out.println("========================================\n");
            
        } catch (SQLException e) {
            System.err.println("Error calling summary stored procedure: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Call stored procedure to add expense with validation
     * Demonstrates stored procedure with both input and output parameters
     */
    public boolean addExpenseWithValidation(int userId, String name, double amount, String category, java.sql.Date date) {
        String sql = "{CALL AddExpenseWithValidation(?, ?, ?, ?, ?, ?)}";
        
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            // Set input parameters
            cstmt.setInt(1, userId);
            cstmt.setString(2, name);
            cstmt.setDouble(3, amount);
            cstmt.setString(4, category);
            cstmt.setDate(5, date);
            
            // Register output parameter (success indicator)
            cstmt.registerOutParameter(6, Types.BOOLEAN);
            
            // Execute stored procedure
            cstmt.execute();
            
            // Get output parameter value
            boolean success = cstmt.getBoolean(6);
            
            System.out.println("Stored procedure AddExpenseWithValidation result: " + success);
            return success;
            
        } catch (SQLException e) {
            System.err.println("Error calling validation stored procedure: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Demonstrate calling a stored procedure with multiple result sets
     * Shows advanced stored procedure handling
     */
    public void demonstrateMultipleResultSets(int userId) {
        String sql = "{CALL GetExpenseAnalysis(?)}";
        
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, userId);
            boolean hasResults = cstmt.execute();
            
            int resultSetCount = 0;
            
            do {
                if (hasResults) {
                    ResultSet rs = cstmt.getResultSet();
                    resultSetCount++;
                    
                    System.out.println("\n=== Result Set " + resultSetCount + " ===");
                    
                    // Print column headers
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.printf("%-15s", metaData.getColumnName(i));
                    }
                    System.out.println();
                    
                    // Print data
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.printf("%-15s", rs.getString(i));
                        }
                        System.out.println();
                    }
                }
                
                hasResults = cstmt.getMoreResults();
                
            } while (hasResults);
            
            System.out.println("Processed " + resultSetCount + " result sets from stored procedure");
            
        } catch (SQLException e) {
            System.err.println("Error with multiple result sets: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
