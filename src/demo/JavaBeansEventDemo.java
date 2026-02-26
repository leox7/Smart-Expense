package demo;

import model.Expense;
import listeners.ExpenseChangeListener;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;

/**
 * Demonstration of JavaBeans events and property change notifications
 * Shows Week 8 concepts: Bound Properties, PropertyChangeSupport, Event Handling
 */
public class JavaBeansEventDemo {
    
    public static void main(String[] args) {
        System.out.println("=== JavaBeans Event Demo (Week 8) ===\n");
        
        // Demo 1: Basic property change events
        demoBasicPropertyChanges();
        
        // Demo 2: Multiple listeners
        demoMultipleListeners();
        
        // Demo 3: Lambda expressions for event handling
        demoLambdaEventHandling();
        
        System.out.println("\n=== Demo Complete ===");
    }
    
    /**
     * Demo 1: Basic property change events
     * Shows how bound properties notify listeners when values change
     */
    private static void demoBasicPropertyChanges() {
        System.out.println("1. Basic Property Change Events:");
        System.out.println("--------------------------------");
        
        // Create expense with event support
        Expense expense = new Expense("Lunch", 15.50, "Food", LocalDate.now());
        
        // Add listener to track changes
        ExpenseChangeListener listener = new ExpenseChangeListener();
        expense.addPropertyChangeListener(listener);
        
        System.out.println("Original expense: " + expense);
        System.out.println("\nChanging properties to trigger events:\n");
        
        // Change properties to trigger events
        expense.setName("Dinner");
        expense.setAmount(25.75);
        expense.setCategory("Entertainment");
        expense.setDate(LocalDate.now().plusDays(1));
        
        System.out.println("\nFinal expense: " + expense);
        System.out.println();
    }
    
    /**
     * Demo 2: Multiple listeners
     * Shows how multiple components can listen to the same bean
     */
    private static void demoMultipleListeners() {
        System.out.println("2. Multiple Listeners:");
        System.out.println("----------------------");
        
        Expense expense = new Expense("Coffee", 4.50, "Food", LocalDate.now());
        
        // Add multiple listeners
        ExpenseChangeListener listener1 = new ExpenseChangeListener();
        expense.addPropertyChangeListener(listener1);
        
        // Second listener using anonymous class
        PropertyChangeListener listener2 = new PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                System.out.println("  [Listener 2] " + evt.getPropertyName() + 
                                 " changed from " + evt.getOldValue() + 
                                 " to " + evt.getNewValue());
            }
        };
        expense.addPropertyChangeListener(listener2);
        
        System.out.println("Added 2 listeners to expense");
        System.out.println("Changing amount to trigger both listeners:\n");
        
        expense.setAmount(5.25);
        
        System.out.println();
    }
    
    /**
     * Demo 3: Lambda expressions for event handling
     * Shows modern Java 8+ approach to event handling
     */
    private static void demoLambdaEventHandling() {
        System.out.println("3. Lambda Event Handling:");
        System.out.println("-------------------------");
        
        Expense expense = new Expense("Book", 29.99, "Education", LocalDate.now());
        
        // Add listener using lambda expression (Java 8+)
        PropertyChangeListener lambdaListener = (evt) -> {
            System.out.println("  [Lambda Listener] Property '" + evt.getPropertyName() + 
                             "' changed from '" + evt.getOldValue() + 
                             "' to '" + evt.getNewValue() + "'");
            
            // Demonstrate conditional logic based on property
            if ("amount".equals(evt.getPropertyName())) {
                Double newAmount = (Double) evt.getNewValue();
                if (newAmount > 50.0) {
                    System.out.println("    → High-value expense detected!");
                }
            }
        };
        
        expense.addPropertyChangeListener(lambdaListener);
        
        System.out.println("Added lambda listener to expense");
        System.out.println("Making changes to trigger lambda listener:\n");
        
        // Trigger events
        expense.setAmount(75.00);  // This will trigger the high-value detection
        expense.setCategory("Books");
        
        System.out.println();
    }
    
    /**
     * Demo 4: Real-world scenario simulation
     * Shows how JavaBeans events might be used in a real application
     */
    public static void demoRealWorldScenario() {
        System.out.println("4. Real-World Scenario:");
        System.out.println("-----------------------");
        
        Expense expense = new Expense("Groceries", 45.80, "Food", LocalDate.now());
        
        // Simulate different components listening to the same expense
        PropertyChangeListener auditLogger = (evt) -> {
            System.out.println("  [Audit Logger] Change logged: " + 
                             evt.getPropertyName() + " = " + evt.getNewValue());
        };
        
        PropertyChangeListener uiUpdater = (evt) -> {
            System.out.println("  [UI Updater] Updating display for " + evt.getPropertyName());
        };
        
        PropertyChangeListener validator = (evt) -> {
            if ("amount".equals(evt.getPropertyName())) {
                Double amount = (Double) evt.getNewValue();
                if (amount < 0) {
                    System.out.println("  [Validator] Warning: Negative amount detected!");
                }
            }
        };
        
        // Add all listeners
        expense.addPropertyChangeListener(auditLogger);
        expense.addPropertyChangeListener(uiUpdater);
        expense.addPropertyChangeListener(validator);
        
        System.out.println("Added audit logger, UI updater, and validator listeners");
        System.out.println("Making changes to demonstrate multi-component communication:\n");
        
        // Trigger events that will notify all listeners
        expense.setAmount(52.30);
        expense.setName("Weekly Groceries");
        
        System.out.println();
    }
}
