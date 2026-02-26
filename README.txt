
💰 SmartExpense – Personal Expense Tracker
A self-learning project exploring Java web development, desktop GUI, and distributed computing concepts using modern Java technologies.
Technologies Used
•	Frontend
o	Java Swing (Desktop GUI)
o	HTML/CSS (Web Interface)
•	Backend
o	Java Servlets
o	HTTP Sessions
•	Server
o	Apache Tomcat 9
•	Distributed Computing
o	Java RMI (Remote Method Invocation)
o	Socket Programming
•	Database
o	MySQL
o	Stored Procedures
•	Build Tool
o	Maven
Project Structure
SmartExpense/
│
├── src/
│   ├── view/        → Swing Desktop GUI
│   ├── web/         → Servlet Controllers
│   ├── dao/         → Database Access Layer
│   ├── rmi/         → RMI Implementations (Week 9)
│   ├── network/     → Socket Programming (Week 11)
│
├── database/        → SQL Schema & Stored Procedures
├── WebContent/      → Web Pages & Static Resources

Running the Web Application
Option 1: NetBeans + Tomcat
1.	Open project
File → Open Project
2.	Configure:
o	Java 17+
o	Apache Tomcat 9
3.	Set Run Properties:
o	Server = Apache Tomcat
o	Context Path = /SmartExpense
4.	Clean and Build project
5.	Run project (deploys automatically to Tomcat)

Option 2: IntelliJ IDEA + Tomcat
1.	Open project folder (Maven auto-detected)
2.	Set Project SDK:
File → Project Structure → JDK 17+
3.	Configure Tomcat:
Settings → Application Servers → Add Tomcat 9
4.	Create Run Configuration:
o	Add Tomcat Server
o	Deploy Artifact
5.	Run Maven:
6.	mvn clean
7.	mvn package
8.	Start Tomcat configuration

🔗 Web Access Points
After deployment, access:
•	Login:
http://localhost:8080/SmartExpense/login.html
•	Dashboard:
http://localhost:8080/SmartExpense/dashboard
•	Add Expense:
http://localhost:8080/SmartExpense/addExpense.html
•	View Expenses:
http://localhost:8080/SmartExpense/viewExpenses

Demo Credentials
Username: admin
Password: password
 Running the Desktop GUI
1.	Navigate to:
2.	src/view/ExpenseForm.java
3.	Run the main() method.

Desktop Features
•	Add expenses
•	Clear form
•	Summary dialog
•	Keyboard shortcuts
o	Enter → Add
o	Esc → Clear
•	Mouse hover tooltips

Week 9: Java RMI Demo
Demonstrates distributed Java using Remote Method Invocation (RMI).

RMI Components
•	GreetingService → Basic RMI Service
•	ExpenseService → RMI Service with Object Serialization

 Running RMI Services
Basic Greeting Service
Start Server
java rmi.GreetingServer [host] [port]
Start Client
java rmi.GreetingClient [host] [port]

 Week 11: Socket Programming
Located in:
src/network/
Demonstrates client-server communication using Java Sockets.

Learning Goals Covered
•	Java Swing GUI Development
•	Java Servlets & Session Management
•	MVC Architecture
•	DAO Pattern
•	MySQL Integration with Stored Procedures
•	Distributed Systems with RMI
•	Network Programming with Sockets
•	Maven Project Structure

