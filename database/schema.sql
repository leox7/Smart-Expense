-- SmartExpense Database Schema
-- Demonstrates JDBC database integration for Weeks 5-6

-- Create database
CREATE DATABASE IF NOT EXISTS smartexpense_db;
USE smartexpense_db;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS users;

-- Users table for authentication
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Expenses table for expense tracking
CREATE TABLE expenses (
    expense_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    expense_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Insert demo users
INSERT INTO users (username, password, email) VALUES 
('admin', 'password', 'admin@smartexpense.com'),
('user1', 'password', 'user1@smartexpense.com'),
('demo', 'demo', 'demo@smartexpense.com');

-- Insert sample expenses
INSERT INTO expenses (user_id, name, amount, category, expense_date) VALUES 
(1, 'Lunch at Cafe', 15.50, 'Food', '2024-01-15'),
(1, 'Bus Ticket', 3.25, 'Transport', '2024-01-15'),
(1, 'Movie Ticket', 12.00, 'Entertainment', '2024-01-14'),
(1, 'Electric Bill', 85.75, 'Bills', '2024-01-13'),
(1, 'Coffee', 4.50, 'Food', '2024-01-13'),
(2, 'Grocery Shopping', 45.80, 'Food', '2024-01-15'),
(2, 'Gas', 35.00, 'Transport', '2024-01-14');

-- Create indexes for better performance
CREATE INDEX idx_expenses_user_id ON expenses(user_id);
CREATE INDEX idx_expenses_date ON expenses(expense_date);
CREATE INDEX idx_expenses_category ON expenses(category);

-- Verify data
SELECT 'Users table:' as info;
SELECT * FROM users;

SELECT 'Expenses table:' as info;
SELECT * FROM expenses;

SELECT 'Expenses with user info:' as info;
SELECT e.expense_id, u.username, e.name, e.amount, e.category, e.expense_date 
FROM expenses e 
JOIN users u ON e.user_id = u.user_id 
ORDER BY e.expense_date DESC;
