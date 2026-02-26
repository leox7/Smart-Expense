package listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Listener for Expense property change events
 * Demonstrates JavaBeans event handling for Week 8
 */
public class ExpenseChangeListener implements PropertyChangeListener {
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        Object oldValue = evt.getOldValue();
        Object newValue = evt.getNewValue();
        
        System.out.println("=== Expense Property Changed ===");
        System.out.println("Property: " + propertyName);
        System.out.println("Old Value: " + oldValue);
        System.out.println("New Value: " + newValue);
        System.out.println("===============================");
        
        // Demonstrate different reactions based on property type
        switch (propertyName) {
            case "amount":
                handleAmountChange((Double) oldValue, (Double) newValue);
                break;
            case "category":
                handleCategoryChange((String) oldValue, (String) newValue);
                break;
            case "name":
                handleNameChange((String) oldValue, (String) newValue);
                break;
            case "date":
                handleDateChange(oldValue, newValue);
                break;
        }
    }
    
    private void handleAmountChange(Double oldAmount, Double newAmount) {
        if (newAmount > oldAmount) {
            System.out.println("  → Amount increased by $" + String.format("%.2f", newAmount - oldAmount));
        } else if (newAmount < oldAmount) {
            System.out.println("  → Amount decreased by $" + String.format("%.2f", oldAmount - newAmount));
        }
    }
    
    private void handleCategoryChange(String oldCategory, String newCategory) {
        System.out.println("  → Category changed from '" + oldCategory + "' to '" + newCategory + "'");
    }
    
    private void handleNameChange(String oldName, String newName) {
        System.out.println("  → Expense name changed from '" + oldName + "' to '" + newName + "'");
    }
    
    private void handleDateChange(Object oldDate, Object newDate) {
        System.out.println("  → Date changed from " + oldDate + " to " + newDate);
    }
}
