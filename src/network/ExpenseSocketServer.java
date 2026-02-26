package network;

import model.Expense;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * ExpenseSocketServer - Multi-Threaded Socket Server for Expense Operations (Week 11)
 * 
 * Demonstrates:
 * - Socket-based expense management service
 * - Multi-threaded server handling multiple clients
 * - Object serialization for Expense transmission (Expense implements Serializable)
 * - Command-based protocol using serialized Command objects
 * - Thread-safe expense storage
 * 
 * Usage:
 *   java network.ExpenseSocketServer [port] [maxThreads]
 *   Default port: 9999
 *   Default max threads: 10
 * 
 * Protocol:
 *   All communication uses ObjectInputStream/ObjectOutputStream
 *   - Client sends Command objects
 *   - Server responds with Response objects
 */
public class ExpenseSocketServer {
    
    private static final int DEFAULT_PORT = 9999;
    private static final int DEFAULT_MAX_THREADS = 10;
    private static final AtomicInteger clientCounter = new AtomicInteger(0);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    // Thread-safe expense storage (in production, use database)
    private static final List<Expense> expenses = new ArrayList<>();
    
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        int maxThreads = DEFAULT_MAX_THREADS;
        
        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port, using default: " + DEFAULT_PORT);
            }
        }
        if (args.length >= 2) {
            try {
                maxThreads = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid max threads, using default: " + DEFAULT_MAX_THREADS);
            }
        }
        
        ExecutorService threadPool = Executors.newFixedThreadPool(maxThreads);
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[ExpenseSocketServer] Server started on port " + port);
            System.out.println("[ExpenseSocketServer] Maximum threads: " + maxThreads);
            System.out.println("[ExpenseSocketServer] Waiting for client connections...");
            System.out.println("[ExpenseSocketServer] Press Ctrl+C to stop the server");
            System.out.println();
            
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    int clientId = clientCounter.incrementAndGet();
                    
                    System.out.println(getTimestamp() + " [Server] Client #" + clientId + 
                                     " connected from: " + clientSocket.getRemoteSocketAddress());
                    
                    ExpenseClientHandler handler = new ExpenseClientHandler(clientSocket, clientId);
                    threadPool.submit(handler);
                    
                } catch (IOException e) {
                    System.err.println(getTimestamp() + " [Server] Error accepting client: " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            System.err.println("[ExpenseSocketServer] Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
            System.out.println("[ExpenseSocketServer] Server shutting down...");
        }
    }
    
    private static String getTimestamp() {
        return LocalDateTime.now().format(timeFormatter);
    }
    
    /**
     * Command class for client-server communication
     */
    public static class Command implements Serializable {
        private static final long serialVersionUID = 1L;
        public String type;
        public Expense expense;
        public String category;
        
        public Command(String type) {
            this.type = type;
        }
        
        public Command(String type, Expense expense) {
            this.type = type;
            this.expense = expense;
        }
        
        public Command(String type, String category) {
            this.type = type;
            this.category = category;
        }
    }
    
    /**
     * Response class for server-client communication
     */
    public static class Response implements Serializable {
        private static final long serialVersionUID = 1L;
        public String status;
        public Object data;
        public String message;
        
        public Response(String status, Object data) {
            this.status = status;
            this.data = data;
        }
        
        public Response(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }
    
    /**
     * ClientHandler for expense operations
     */
    private static class ExpenseClientHandler implements Runnable {
        private final Socket clientSocket;
        private final int clientId;
        
        public ExpenseClientHandler(Socket socket, int clientId) {
            this.clientSocket = socket;
            this.clientId = clientId;
        }
        
        @Override
        public void run() {
            try (ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());
                 ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream())) {
                
                // Send initial connection acknowledgment
                objectOut.writeObject(new Response("CONNECTED", "Welcome to Expense Server"));
                objectOut.flush();
                
                Command command;
                while ((command = (Command) objectIn.readObject()) != null) {
                    String timestamp = getTimestamp();
                    System.out.println(timestamp + " [Client #" + clientId + "] Command: " + command.type);
                    
                    Response response = processCommand(command);
                    objectOut.writeObject(response);
                    objectOut.flush();
                    
                    if (response.status.equals("SUCCESS") && command.type.equals("BYE")) {
                        System.out.println(timestamp + " [Server] Client #" + clientId + " requested disconnect");
                        break;
                    }
                }
                
            } catch (IOException e) {
                if (!clientSocket.isClosed()) {
                    System.err.println(getTimestamp() + " [Server] Error with Client #" + clientId + ": " + e.getMessage());
                }
            } catch (ClassNotFoundException e) {
                System.err.println(getTimestamp() + " [Server] Error reading command from Client #" + clientId + ": " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                    System.out.println(getTimestamp() + " [Server] Client #" + clientId + " disconnected");
                } catch (IOException e) {
                    System.err.println(getTimestamp() + " [Server] Error closing Client #" + clientId + " socket");
                }
            }
        }
        
        private Response processCommand(Command command) {
            synchronized (expenses) { // Thread-safe access to expenses list
                String cmd = command.type.toUpperCase();
                
                try {
                    switch (cmd) {
                        case "ADD":
                            if (command.expense != null) {
                                expenses.add(command.expense);
                                System.out.println(getTimestamp() + " [Server] Added expense: " + command.expense);
                                return new Response("SUCCESS", "Expense added: " + command.expense.getName() + " ($" + command.expense.getAmount() + ")");
                            }
                            return new Response("ERROR", "Expense data missing");
                            
                        case "GET_ALL":
                            List<Expense> allExpenses = new ArrayList<>(expenses);
                            System.out.println(getTimestamp() + " [Server] Sending " + allExpenses.size() + " expenses to Client #" + clientId);
                            return new Response("SUCCESS", allExpenses);
                            
                        case "GET_BY_CATEGORY":
                            if (command.category != null) {
                                List<Expense> filtered = expenses.stream()
                                        .filter(e -> e.getCategory() != null && 
                                                e.getCategory().equalsIgnoreCase(command.category))
                                        .collect(Collectors.toList());
                                System.out.println(getTimestamp() + " [Server] Sending " + filtered.size() + 
                                                 " expenses for category '" + command.category + "' to Client #" + clientId);
                                return new Response("SUCCESS", filtered);
                            }
                            return new Response("ERROR", "Category missing");
                            
                        case "GET_TOTAL":
                            double total = expenses.stream()
                                    .mapToDouble(Expense::getAmount)
                                    .sum();
                            System.out.println(getTimestamp() + " [Server] Total: $" + total);
                            return new Response("SUCCESS", total);
                            
                        case "COUNT":
                            int count = expenses.size();
                            System.out.println(getTimestamp() + " [Server] Count: " + count);
                            return new Response("SUCCESS", count);
                            
                        case "BYE":
                            return new Response("SUCCESS", "Goodbye");
                            
                        default:
                            return new Response("ERROR", "Unknown command: " + cmd);
                    }
                } catch (Exception e) {
                    System.err.println(getTimestamp() + " [Server] Error processing command: " + e.getMessage());
                    e.printStackTrace();
                    return new Response("ERROR", "Server error: " + e.getMessage());
                }
            }
        }
    }
}
