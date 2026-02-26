package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Expense implements Serializable {
    private String name;
    private double amount;
    private String category;
    private LocalDate date;
    
    // JavaBeans event support
    private PropertyChangeSupport propertyChangeSupport;

    public Expense() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public Expense(String name, double amount, String category, LocalDate date) {
        this();
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }
    
    // Property change support methods
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        propertyChangeSupport.firePropertyChange("name", oldName, name);
    }

    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        double oldAmount = this.amount;
        this.amount = amount;
        propertyChangeSupport.firePropertyChange("amount", oldAmount, amount);
    }

    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        String oldCategory = this.category;
        this.category = category;
        propertyChangeSupport.firePropertyChange("category", oldCategory, category);
    }

    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        LocalDate oldDate = this.date;
        this.date = date;
        propertyChangeSupport.firePropertyChange("date", oldDate, date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Double.compare(expense.amount, amount) == 0 &&
                Objects.equals(name, expense.name) &&
                Objects.equals(category, expense.category) &&
                Objects.equals(date, expense.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, amount, category, date);
    }

    @Override
    public String toString() {
        return "Expense{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", date=" + date +
                '}';
    }
}


