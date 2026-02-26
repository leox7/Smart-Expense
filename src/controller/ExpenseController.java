package controller;

import dao.ExpenseDAO;
import dao.ExpenseStoredProcedures;
import dao.DatabaseConnection;
import listeners.ExpenseChangeListener;
import model.Expense;
import view.ExpenseForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ExpenseController implements ActionListener, KeyListener, MouseListener {
    private final ExpenseForm view;
    private final List<Expense> expenses = new ArrayList<>();
    private final ExpenseDAO expenseDAO;
    private final ExpenseStoredProcedures storedProcedures;
    private final int currentUserId = 1; // Demo user ID - in real app, get from login

    public ExpenseController(ExpenseForm view) {
        this.view = view;
        this.expenseDAO = new ExpenseDAO();
        this.storedProcedures = new ExpenseStoredProcedures();
        
        // Test database connection on startup
        if (DatabaseConnection.testConnection()) {
            loadExpensesFromDatabase();
        } else {
            JOptionPane.showMessageDialog(view, 
                "Database connection failed! Using in-memory storage only.", 
                "Database Warning", 
                JOptionPane.WARNING_MESSAGE);
        }
        
        // Setup JavaBeans event handling for Week 8 demonstration
        setupExpenseEventHandling();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == view.getAddButton()) {
            handleAddExpense();
        } else if (source == view.getClearButton()) {
            view.clearInputs();
        } else if (source == view.getExitButton()) {
            System.exit(0);
        } else if (source == view.getSummaryButton()) {
            showSummaryDialog();
        } else if (source == view.getEventDemoButton()) {
            demonstrateExpenseEvents();
        }
    }

    private void handleAddExpense() {
        String name = view.getExpenseNameInput();
        String amountText = view.getAmountInput();
        String category = view.getCategoryInput();
        String dateText = view.getDateInput();

        if (name.isEmpty() || amountText.isEmpty() || dateText.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please fill in all fields.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount < 0) throw new NumberFormatException("negative");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Amount must be a positive number.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateText);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(view, "Date must be in YYYY-MM-DD format.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Expense expense = new Expense(name, amount, category, date);
        
        // Add JavaBeans event listener to track changes (Week 8 demonstration)
        ExpenseChangeListener listener = new ExpenseChangeListener();
        expense.addPropertyChangeListener(listener);
        
        // Try to save to database first
        boolean savedToDatabase = expenseDAO.addExpense(expense, currentUserId);
        
        if (savedToDatabase) {
            // Add to local list and update UI
            expenses.add(expense);
            DefaultTableModel model = view.getTableModel();
            model.addRow(new Object[]{expense.getName(), expense.getAmount(), expense.getCategory(), expense.getDate()});
            view.clearInputs();
            
            System.out.println("Expense saved to database successfully!");
        } else {
            // Fallback to in-memory storage
            expenses.add(expense);
            DefaultTableModel model = view.getTableModel();
            model.addRow(new Object[]{expense.getName(), expense.getAmount(), expense.getCategory(), expense.getDate()});
            view.clearInputs();
            
            JOptionPane.showMessageDialog(view, 
                "Saved to memory only (database unavailable).", 
                "Database Notice", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showSummaryDialog() {
        // Show local summary
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        long count = expenses.size();
        
        // Also show database statistics if available
        if (DatabaseConnection.testConnection()) {
            expenseDAO.printExpenseStatistics(currentUserId);
            storedProcedures.printExpenseSummary(currentUserId);
        }
        
        JOptionPane.showMessageDialog(view,
                "Local Expenses:\n" +
                "Total: $" + String.format("%.2f", total) + "\n" +
                "Items: " + count + "\n\n" +
                "Check console for database statistics.",
                "Expense Summary",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Load expenses from database into local list and UI
     * Demonstrates ResultSet processing and UI updates
     * Also adds JavaBeans event listeners for Week 8 demonstration
     */
    private void loadExpensesFromDatabase() {
        List<Expense> dbExpenses = expenseDAO.getExpensesByUser(currentUserId);
        
        // Clear existing data
        expenses.clear();
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        
        // Load from database and add event listeners
        for (Expense expense : dbExpenses) {
            // Add JavaBeans event listener to track property changes
            ExpenseChangeListener listener = new ExpenseChangeListener();
            expense.addPropertyChangeListener(listener);
            
            expenses.add(expense);
            model.addRow(new Object[]{expense.getName(), expense.getAmount(), expense.getCategory(), expense.getDate()});
        }
        
        System.out.println("Loaded " + dbExpenses.size() + " expenses from database");
        System.out.println("Added JavaBeans event listeners to all loaded expenses");
    }
    
    /**
     * Demonstrate stored procedure usage
     */
    public void demonstrateStoredProcedures() {
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(view, "Database not available for stored procedure demo.");
            return;
        }
        
        // Get expenses by category using stored procedure
        List<Expense> foodExpenses = storedProcedures.getExpensesByUserAndCategory(currentUserId, "Food");
        
        StringBuilder message = new StringBuilder();
        message.append("Stored Procedure Demo:\n");
        message.append("Food expenses found: ").append(foodExpenses.size()).append("\n\n");
        
        for (Expense expense : foodExpenses) {
            message.append("• ").append(expense.getName())
                   .append(" - $").append(String.format("%.2f", expense.getAmount()))
                   .append(" (").append(expense.getDate()).append(")\n");
        }
        
        JOptionPane.showMessageDialog(view, message.toString(), "Stored Procedure Results", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Setup JavaBeans event handling for Week 8 demonstration
     * Shows how to add event listeners to track property changes
     */
    private void setupExpenseEventHandling() {
        System.out.println("=== JavaBeans Event Handling Setup (Week 8) ===");
        System.out.println("Event listeners will track property changes in expenses");
        System.out.println("===============================================");
    }
    
    /**
     * Demonstrate JavaBeans events by modifying an existing expense
     * This shows how property change events work in practice
     */
    public void demonstrateExpenseEvents() {
        if (expenses.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No expenses available for event demonstration.\nAdd some expenses first.", 
                "Event Demo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Get the first expense for demonstration
        Expense demoExpense = expenses.get(0);
        
        // Add event listener if not already added
        ExpenseChangeListener listener = new ExpenseChangeListener();
        demoExpense.addPropertyChangeListener(listener);
        
        System.out.println("\n=== JavaBeans Event Demonstration ===");
        System.out.println("Original expense: " + demoExpense);
        System.out.println("Making changes to demonstrate property change events:\n");
        
        // Demonstrate property changes that will trigger events
        demoExpense.setAmount(demoExpense.getAmount() + 5.00);
        demoExpense.setName(demoExpense.getName() + " (Updated)");
        demoExpense.setCategory("Modified " + demoExpense.getCategory());
        
        System.out.println("Final expense: " + demoExpense);
        System.out.println("=====================================\n");
        
        JOptionPane.showMessageDialog(view, 
            "JavaBeans event demonstration completed!\nCheck console for event notifications.", 
            "Event Demo Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    // KeyListener for quick actions: Enter to Add, Esc to Clear
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            handleAddExpense();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            view.clearInputs();
        }
    }
    @Override public void keyReleased(KeyEvent e) {}

    // MouseListener for simple interaction: show tooltip with amount on row click
    @Override public void mouseClicked(MouseEvent e) {
        JTable table = (JTable) e.getSource();
        int row = table.getSelectedRow();
        if (row >= 0 && row < expenses.size()) {
            Expense exp = expenses.get(row);
            table.setToolTipText("Selected: " + exp.getName() + " (" + exp.getCategory() + ") - " + exp.getAmount());
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}


