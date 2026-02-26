package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MultiEchoServer - Multi-Threaded Socket Server (Week 11)
 * 
 * Demonstrates:
 * - Multi-client support using threads
 * - ExecutorService for thread management
 * - Concurrent client connections
 * - Thread-safe logging
 * - Client session management
 * 
 * Usage:
 *   java network.MultiEchoServer [port] [maxThreads]
 *   Default port: 8888
 *   Default max threads: 10
 * 
 * Features:
 * - Handles multiple clients simultaneously
 * - Each client runs in a separate thread
 * - Logs all messages with client ID and timestamp
 * - Graceful shutdown support
 */
public class MultiEchoServer {
    
    private static final int DEFAULT_PORT = 8888;
    private static final int DEFAULT_MAX_THREADS = 10;
    private static final AtomicInteger clientCounter = new AtomicInteger(0);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
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
        
        // Create thread pool for handling clients
        ExecutorService threadPool = Executors.newFixedThreadPool(maxThreads);
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[MultiEchoServer] Server started on port " + port);
            System.out.println("[MultiEchoServer] Maximum threads: " + maxThreads);
            System.out.println("[MultiEchoServer] Waiting for client connections...");
            System.out.println("[MultiEchoServer] Press Ctrl+C to stop the server");
            System.out.println();
            
            // Continuously accept client connections
            while (true) {
                try {
                    // Accept client connection (blocks until client connects)
                    Socket clientSocket = serverSocket.accept();
                    int clientId = clientCounter.incrementAndGet();
                    
                    System.out.println(getTimestamp() + " [Server] Client #" + clientId + 
                                     " connected from: " + clientSocket.getRemoteSocketAddress());
                    
                    // Handle client in a separate thread
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clientId);
                    threadPool.submit(clientHandler);
                    
                } catch (IOException e) {
                    System.err.println(getTimestamp() + " [Server] Error accepting client: " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            System.err.println("[MultiEchoServer] Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Shutdown thread pool
            threadPool.shutdown();
            System.out.println("[MultiEchoServer] Server shutting down...");
        }
    }
    
    private static String getTimestamp() {
        return LocalDateTime.now().format(timeFormatter);
    }
    
    /**
     * ClientHandler - Handles communication with a single client
     * Runs in a separate thread to allow concurrent client connections
     */
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final int clientId;
        
        public ClientHandler(Socket socket, int clientId) {
            this.clientSocket = socket;
            this.clientId = clientId;
        }
        
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                
                String inputLine;
                // Read messages from client
                while ((inputLine = in.readLine()) != null) {
                    String timestamp = getTimestamp();
                    
                    // Log message from client
                    System.out.println(timestamp + " [Client #" + clientId + "] " + inputLine);
                    
                    // Echo message back to client
                    String response = "Echo: " + inputLine;
                    out.println(response);
                    System.out.println(timestamp + " [Server -> Client #" + clientId + "] " + response);
                    
                    // Exit if client sends "bye"
                    if ("bye".equalsIgnoreCase(inputLine.trim())) {
                        System.out.println(getTimestamp() + " [Server] Client #" + clientId + " requested disconnect");
                        break;
                    }
                }
                
            } catch (IOException e) {
                System.err.println(getTimestamp() + " [Server] Error with Client #" + clientId + ": " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                    System.out.println(getTimestamp() + " [Server] Client #" + clientId + " disconnected");
                } catch (IOException e) {
                    System.err.println(getTimestamp() + " [Server] Error closing Client #" + clientId + " socket");
                }
            }
        }
    }
}

