-- SmartExpense Stored Procedures
-- Demonstrates CallableStatement usage for Week 6

USE smartexpense_db;

-- Drop existing procedures if they exist
DROP PROCEDURE IF EXISTS GetExpensesByUserAndCategory;
DROP PROCEDURE IF EXISTS GetExpenseSummary;
DROP PROCEDURE IF EXISTS AddExpenseWithValidation;
DROP PROCEDURE IF EXISTS GetExpenseAnalysis;

DELIMITER //

-- Procedure 1: Get expenses by user and category
-- Demonstrates basic stored procedure with input parameters
CREATE PROCEDURE GetExpensesByUserAndCategory(
    IN p_user_id INT,
    IN p_category VARCHAR(50)
)
BEGIN
    SELECT expense_id, name, amount, category, expense_date, created_at
    FROM expenses 
    WHERE user_id = p_user_id AND category = p_category
    ORDER BY expense_date DESC;
END //

-- Procedure 2: Get expense summary by category for a user
-- Demonstrates aggregate functions in stored procedures
CREATE PROCEDURE GetExpenseSummary(IN p_user_id INT)
BEGIN
    SELECT 
        category,
        COUNT(*) as count,
        SUM(amount) as total,
        AVG(amount) as average,
        MIN(amount) as min_amount,
        MAX(amount) as max_amount
    FROM expenses 
    WHERE user_id = p_user_id 
    GROUP BY category
    ORDER BY total DESC;
END //

-- Procedure 3: Add expense with validation
-- Demonstrates stored procedure with input and output parameters
CREATE PROCEDURE AddExpenseWithValidation(
    IN p_user_id INT,
    IN p_name VARCHAR(100),
    IN p_amount DECIMAL(10,2),
    IN p_category VARCHAR(50),
    IN p_expense_date DATE,
    OUT p_success BOOLEAN
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET p_success = FALSE;
        ROLLBACK;
    END;
    
    START TRANSACTION;
    
    -- Validate inputs
    IF p_amount <= 0 THEN
        SET p_success = FALSE;
        ROLLBACK;
    ELSEIF p_user_id NOT IN (SELECT user_id FROM users) THEN
        SET p_success = FALSE;
        ROLLBACK;
    ELSE
        -- Insert expense
        INSERT INTO expenses (user_id, name, amount, category, expense_date)
        VALUES (p_user_id, p_name, p_amount, p_category, p_expense_date);
        
        SET p_success = TRUE;
        COMMIT;
    END IF;
END //

-- Procedure 4: Get comprehensive expense analysis
-- Demonstrates multiple result sets from stored procedure
CREATE PROCEDURE GetExpenseAnalysis(IN p_user_id INT)
BEGIN
    -- Result Set 1: Recent expenses
    SELECT 'Recent Expenses' as analysis_type, name, amount, category, expense_date
    FROM expenses 
    WHERE user_id = p_user_id 
    ORDER BY expense_date DESC 
    LIMIT 5;
    
    -- Result Set 2: Category breakdown
    SELECT 'Category Breakdown' as analysis_type, category, COUNT(*) as count, SUM(amount) as total
    FROM expenses 
    WHERE user_id = p_user_id 
    GROUP BY category;
    
    -- Result Set 3: Monthly totals
    SELECT 'Monthly Totals' as analysis_type, 
           YEAR(expense_date) as year,
           MONTH(expense_date) as month,
           COUNT(*) as count,
           SUM(amount) as total
    FROM expenses 
    WHERE user_id = p_user_id 
    GROUP BY YEAR(expense_date), MONTH(expense_date)
    ORDER BY year DESC, month DESC;
END //

DELIMITER ;

-- Test the stored procedures
SELECT 'Testing stored procedures...' as info;

-- Test 1: Get expenses by category
CALL GetExpensesByUserAndCategory(1, 'Food');

-- Test 2: Get expense summary
CALL GetExpenseSummary(1);

-- Test 3: Add expense with validation (success case)
SET @success = FALSE;
CALL AddExpenseWithValidation(1, 'Test Expense', 25.50, 'Entertainment', '2024-01-16', @success);
SELECT @success as add_expense_result;

-- Test 4: Add expense with validation (failure case - negative amount)
SET @success = FALSE;
CALL AddExpenseWithValidation(1, 'Invalid Expense', -10.00, 'Food', '2024-01-16', @success);
SELECT @success as add_invalid_expense_result;

-- Test 5: Get expense analysis
CALL GetExpenseAnalysis(1);

SELECT 'Stored procedures created and tested successfully!' as status;
