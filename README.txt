SmartExpense - Personal Expense Tracker (Swing + Servlets + Sessions)

How to run (NetBeans + Tomcat):
1) Open NetBeans, File > Open Project, select the SmartExpense folder.
2) Ensure Java 17+ and Apache Tomcat are registered in NetBeans (Services > Servers).
3) Right-click project > Properties > Run. Set Server: Apache Tomcat, Context Path: /SmartExpense.
4) Build: Right-click project > Clean and Build (produces dist/SmartExpense.war).
5) Run: Right-click project > Run. The app will deploy to Tomcat.

Web URLs:
- Login page: http://localhost:8080/SmartExpense/login.html
- After login, dashboard: http://localhost:8080/SmartExpense/dashboard
- Add expense form: http://localhost:8080/SmartExpense/addExpense.html
- View expenses: http://localhost:8080/SmartExpense/viewExpenses

Demo credentials:
- username: admin
- password: password

Desktop GUI (Swing):
- Run main() in src/view/ExpenseForm.java.
- Features: add, clear, exit, summary dialog; keyboard shortcuts (Enter=add, Esc=clear), mouse row tooltip.

Notes:
- Session stores expenses in-memory; per-user session only, no database.
- Uses @WebServlet annotations; minimal web.xml for war completeness.

How to run (IntelliJ IDEA + Tomcat 9):
1) File > Open, select the SmartExpense folder. IntelliJ will detect Maven (pom.xml).
2) Ensure JDK 17 is the Project SDK (File > Project Structure > Project).
3) Install and configure Apache Tomcat 9.x (File > Settings > Build, Execution, Deployment > Application Servers).
4) Add Run Configuration: Run > Edit Configurations > + > Tomcat Server (Local).
   - Application server: your Tomcat 9 install.
   - Deployment: + > Artifact > SmartExpense:war exploded. Set Application context: /SmartExpense
5) Build from Maven tool window: Lifecycle > clean, then package (creates target/SmartExpense.war).
6) Start the Tomcat run configuration. Open http://localhost:8080/SmartExpense/login.html

Notes for IntelliJ/Tomcat:
- This project uses javax.servlet.*; use Tomcat 9.x. If you use Tomcat 10+, migrate imports to jakarta.* and update web.xml to Servlet 5.
- The Servlet API is provided scope via Maven (javax.servlet:javax.servlet-api:4.0.1), so you don't need to add jars manually.


# --- Week 9: Java RMI Demo ---
This project includes a minimal Java RMI demo in `src/rmi/` to demonstrate distributed Java concepts (see Week 9 Focus).

**RMI Components:**
- `GreetingService.java` – Remote interface (extends Remote): defines `sayHello(String name)`
- `GreetingServiceImpl.java` – Implementation (extends UnicastRemoteObject): provides the logic
- `GreetingServer.java` – Starts the RMI registry, binds the GreetingService instance
- `GreetingClient.java` – Looks up the remote service, calls `sayHello` and prints the result

**To run the demo locally:**
1. Compile all RMI classes (`GreetingService`, `GreetingServiceImpl`, `GreetingServer`, `GreetingClient`)
2. Run the server: `java rmi.GreetingServer` (starts RMI registry and service)
3. In another console, run the client: `java rmi.GreetingClient`
4. Observe server and client console outputs, showing remote method invocation

**Key RMI Concepts Illustrated in Code:**
- Remote interface `GreetingService extends Remote`
- Server implementation extends `UnicastRemoteObject`
- Explicit registry binding and lookup via `Naming.rebind` and `Naming.lookup`
- Exception handling (`RemoteException`)
- Transfer of parameters and results (object serialization)

This demo matches all required Week 9 RMI fundamentals for distributed Java labs.


# --- Week 10: RMI Network Deployment & Parameter Handling ---

## Enhanced RMI Features

### 1. Network Deployment Support
All RMI servers and clients now support network deployment with configurable host and port.

**GreetingService (Enhanced):**
- `GreetingServer.java` – Enhanced with network deployment support
- `GreetingClient.java` – Enhanced with remote IP connection support

**Usage:**
```bash
# Server on machine with IP 192.168.1.100
java rmi.GreetingServer 192.168.1.100 1099

# Client connecting from another machine
java rmi.GreetingClient 192.168.1.100 1099
```

### 2. ExpenseService RMI (New)
Complete RMI service for Expense objects demonstrating object serialization.

**Components:**
- `ExpenseService.java` – Remote interface with methods for Expense operations
- `ExpenseServiceImpl.java` – Implementation handling Expense objects
- `ExpenseServer.java` – Server with network deployment support
- `ExpenseClient.java` – Client with remote connection support

**Features:**
- Add expenses remotely
- Retrieve all expenses
- Filter by category
- Calculate totals
- String concatenation demo (parameter passing)

**Usage:**
```bash
# Server (localhost or network IP)
java rmi.ExpenseServer [host] [port]
# Example: java rmi.ExpenseServer 192.168.1.100 1099

# Client
java rmi.ExpenseClient [host] [port]
# Example: java rmi.ExpenseClient 192.168.1.100 1099
```

### 3. Network Deployment Requirements

**Firewall Configuration:**
- Open port 1099 (or custom port) in firewall
- Windows: Windows Firewall > Advanced Settings > Inbound Rules
- Linux: `sudo ufw allow 1099/tcp`

**IP Binding:**
- Server must bind to actual IP address, not localhost
- Use machine's network IP (e.g., 192.168.1.100)
- Find IP: `ipconfig` (Windows) or `ifconfig` (Linux)

**Security Policy (Optional):**
- Policy file: `policy/rmi.policy`
- Usage: `java -Djava.security.policy=policy/rmi.policy rmi.ExpenseServer`

### 4. Key Concepts Demonstrated

**Parameter Passing by Value:**
- Expense objects are serialized when passed between client and server
- Changes to objects on client don't affect server-side objects
- Demonstrates RMI's pass-by-value semantics

**Object Serialization:**
- Expense class implements Serializable (required for RMI)
- Objects are automatically serialized/deserialized
- LocalDate fields are properly serialized

**Cross-Platform Deployment:**
- Works across different operating systems (Windows/Linux)
- Compatible with different JDK versions (JDK 17+)
- Requires same class versions on client and server

### 5. Troubleshooting

**Common Issues:**
1. **Connection refused**: Check if server is running and firewall allows port
2. **ClassNotFoundException**: Ensure Expense class is in classpath on both client and server
3. **Registry errors**: Port may be in use; try different port
4. **Network unreachable**: Verify IP address and network connectivity


# --- Week 11: Socket Programming & Multi-Threading ---

## Socket Communication Implementations

### 1. Echo Server (Single-Client)
Basic socket server demonstrating client-server communication.

**Components:**
- `EchoServer.java` – Single-client echo server
- `EchoClient.java` – Client for echo server

**Features:**
- Accepts one client connection
- Echoes messages back to client
- Demonstrates Socket and ServerSocket usage
- Input/Output stream handling

**Usage:**
```bash
# Server
java network.EchoServer [port]
# Default port: 8888

# Client
java network.EchoClient [host] [port]
# Example: java network.EchoClient localhost 8888
```

### 2. Multi-Threaded Echo Server
Multi-client socket server using thread pool.

**Components:**
- `MultiEchoServer.java` – Multi-threaded echo server
- `EchoClient.java` – Same client (supports multiple clients)

**Features:**
- Handles multiple clients simultaneously
- Uses ExecutorService for thread management
- Thread-safe logging with client IDs
- Configurable thread pool size

**Usage:**
```bash
# Server
java network.MultiEchoServer [port] [maxThreads]
# Default: port 8888, maxThreads 10

# Multiple clients can connect simultaneously
java network.EchoClient localhost 8888
```

### 3. Expense Socket Server (Multi-Threaded)
Complete socket-based expense management service.

**Components:**
- `ExpenseSocketServer.java` – Multi-threaded expense server
- `ExpenseSocketClient.java` – Client for expense operations

**Features:**
- Multi-threaded server handling multiple clients
- Object serialization for Expense transmission
- Command-based protocol (ADD, GET_ALL, GET_BY_CATEGORY, GET_TOTAL, COUNT, BYE)
- Thread-safe expense storage
- Uses ObjectInputStream/ObjectOutputStream

**Commands:**
- `ADD` – Add an expense (prompts for details)
- `GET_ALL` – Get all expenses
- `GET_BY_CATEGORY` – Get expenses by category
- `GET_TOTAL` – Get total amount
- `COUNT` – Get expense count
- `BYE` – Disconnect

**Usage:**
```bash
# Server
java network.ExpenseSocketServer [port] [maxThreads]
# Default: port 9999, maxThreads 10

# Client
java network.ExpenseSocketClient [host] [port]
# Example: java network.ExpenseSocketClient localhost 9999
```

### 4. Key Concepts Demonstrated

**Socket Communication:**
- ServerSocket for accepting connections
- Socket for client-server communication
- Input/Output streams for data exchange

**Multi-Threading:**
- ExecutorService for thread pool management
- Runnable interface for client handlers
- Thread-safe data structures (synchronized blocks)
- AtomicInteger for thread-safe counters

**Object Serialization:**
- Expense objects serialized via ObjectOutputStream
- Command/Response objects for protocol communication
- Serializable interface implementation

**Concurrent Client Handling:**
- Each client runs in separate thread
- Thread-safe access to shared data (expenses list)
- Proper resource cleanup (try-with-resources)

### 5. Protocol Details

**ExpenseSocketServer Protocol:**
- All communication uses ObjectInputStream/ObjectOutputStream
- Client sends Command objects
- Server responds with Response objects
- Command types: ADD, GET_ALL, GET_BY_CATEGORY, GET_TOTAL, COUNT, BYE

**Echo Server Protocol:**
- Text-based communication
- Client sends text messages
- Server echoes messages back
- "bye" command disconnects client

### 6. Troubleshooting

**Common Issues:**
1. **Port already in use**: Change port number or stop other service using the port
2. **Connection refused**: Ensure server is running before starting client
3. **ClassNotFoundException**: Ensure Expense class is in classpath
4. **Socket timeout**: Check firewall settings and network connectivity
5. **Multiple stream issue**: Don't mix BufferedReader with ObjectInputStream on same stream
