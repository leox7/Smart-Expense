# SmartExpense Database Setup Instructions

## Prerequisites
1. **Install MySQL Server**
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Or use MySQL Workbench for GUI management
   - Default port: 3306
   - Remember your root password

## Database Setup Steps

### 1. Create Database and Tables
```bash
# Connect to MySQL as root user
mysql -u root -p

# Run the schema file
source /path/to/SmartExpense/database/schema.sql
```

Or using MySQL Workbench:
- Open MySQL Workbench
- Connect to your MySQL server
- File → Open SQL Script → Select `database/schema.sql`
- Execute the script

### 2. Create Stored Procedures
```bash
# Run the stored procedures file
source /path/to/SmartExpense/database/stored_procedures.sql
```

Or using MySQL Workbench:
- File → Open SQL Script → Select `database/stored_procedures.sql`
- Execute the script

### 3. Update Database Connection
Edit `src/dao/DatabaseConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/smartexpense_db?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password"; // Change this!
```

### 4. Test Database Connection
Run the desktop application (`ExpenseForm.main()`) to test the connection. You should see:
```
MySQL JDBC Driver loaded successfully
Database connection successful!
```

## Verification

### Check Database Contents
```sql
USE smartexpense_db;

-- View users
SELECT * FROM users;

-- View expenses
SELECT * FROM expenses;

-- View expenses with user info
SELECT e.expense_id, u.username, e.name, e.amount, e.category, e.expense_date 
FROM expenses e 
JOIN users u ON e.user_id = u.user_id 
ORDER BY e.expense_date DESC;
```

### Test Stored Procedures
```sql
-- Test expense summary
CALL GetExpenseSummary(1);

-- Test expenses by category
CALL GetExpensesByUserAndCategory(1, 'Food');

-- Test expense analysis
CALL GetExpenseAnalysis(1);
```

## Troubleshooting

### Connection Issues
- **Error: "Access denied"**: Check username/password in `DatabaseConnection.java`
- **Error: "Unknown database"**: Run `schema.sql` to create the database
- **Error: "Driver not found"**: Ensure MySQL Connector/J is in classpath (Maven dependency)

### Port Issues
- **Error: "Connection refused"**: Check if MySQL is running on port 3306
- **Windows**: Check Services → MySQL80 (or your version)
- **Linux/Mac**: `sudo service mysql start` or `brew services start mysql`

### Common Commands
```bash
# Start MySQL service (Windows)
net start MySQL80

# Start MySQL service (Linux/Mac)
sudo service mysql start

# Connect to MySQL
mysql -u root -p

# Check if MySQL is running
netstat -an | grep 3306
```

## Demo Data
The schema includes sample data:
- **Users**: admin/password, user1/password, demo/demo
- **Expenses**: Various sample expenses for testing

## JDBC Concepts Demonstrated

### Week 5 Topics
1. **DriverManager**: Loading MySQL JDBC driver
2. **Connection**: Database connection management
3. **PreparedStatement**: SQL injection prevention
4. **ResultSet**: Processing query results
5. **Try-with-resources**: Automatic resource cleanup

### Week 6 Topics
1. **ResultSetMetaData**: Analyzing table structure
2. **CallableStatement**: Calling stored procedures
3. **Multiple ResultSets**: Processing complex stored procedure results
4. **Error Handling**: Exception management and logging

## Next Steps
1. Set up the database using the instructions above
2. Update the password in `DatabaseConnection.java`
3. Build and run the project
4. Test both desktop and web applications
5. Check server console for JDBC demonstration output
