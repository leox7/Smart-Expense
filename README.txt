# SmartExpense - Personal Expense Tracker

A self-learning project exploring Java web development, desktop GUI, and distributed computing concepts.

## Technologies Used
- **Frontend**: Java Swing (desktop), HTML/CSS (web)
- **Backend**: Java Servlets, Sessions
- **Server**: Apache Tomcat 9
- **Distributed Computing**: Java RMI, Socket Programming
- **Build**: Maven
- **Database**: MySQL (with stored procedures)

## Project Structure
- `src/view/` - Swing desktop GUI
- `src/web/` - Servlet controllers
- `src/dao/` - Database access layer
- `src/rmi/` - RMI implementations (Week 9)
- `src/network/` - Socket programming (Week 11)
- `database/` - SQL schema and stored procedures
- `WebContent/` - Web pages and resources

## Running the Web Application

### NetBeans + Tomcat
1. Open project in NetBeans (File > Open Project)
2. Ensure Java 17+ and Apache Tomcat 9 are configured
3. Set Run properties: Server = Apache Tomcat, Context Path = /SmartExpense
4. Clean and Build project
5. Run project (deploys to Tomcat)

### IntelliJ IDEA + Tomcat
1. Open project folder (IntelliJ detects Maven)
2. Set Project SDK to JDK 17+ (File > Project Structure)
3. Configure Tomcat 9 (Settings > Application Servers)
4. Create Tomcat Run Configuration with artifact deployment
5. Build with Maven (clean, then package)
6. Start Tomcat configuration

### Access Points
- Login: http://localhost:8080/SmartExpense/login.html
- Dashboard: http://localhost:8080/SmartExpense/dashboard
- Add Expense: http://localhost:8080/SmartExpense/addExpense.html
- View Expenses: http://localhost:8080/SmartExpense/viewExpenses

**Demo Credentials:** username: `admin`, password: `password`

## Running the Desktop GUI
Run `main()` in `src/view/ExpenseForm.java`

**Features:**
- Add/clear expenses
- Summary dialog
- Keyboard shortcuts (Enter=add, Esc=clear)
- Mouse hover tooltips

## Week 9: Java RMI Demo

Demonstrates distributed Java with Remote Method Invocation.

### Components
- `GreetingService` - Basic RMI service
- `ExpenseService` - RMI service with object serialization

### Running RMI Services

**Basic Greeting Service:**
# Server
java rmi.GreetingServer [host] [port]

# Client
java rmi.GreetingClient [host] [port]
