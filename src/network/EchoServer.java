package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * EchoServer - Single-Client Socket Server (Week 11)
 * 
 * Demonstrates:
 * - Basic client-server communication using Sockets
 * - ServerSocket for accepting connections
 * - Input/Output streams for data exchange
 * - Single-client handling (blocks until client disconnects)
 * 
 * Usage:
 *   java network.EchoServer [port]
 *   Default port: 8888
 * 
 * This is a simple demonstration. For multiple clients, use MultiEchoServer.
 */
public class EchoServer {
    
    private static final int DEFAULT_PORT = 8888;
    
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        
        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port, using default: " + DEFAULT_PORT);
            }
        }
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[EchoServer] Server started on port " + port);
            System.out.println("[EchoServer] Waiting for client connection...");
            
            // Accept a client connection (blocks until client connects)
            Socket clientSocket = serverSocket.accept();
            System.out.println("[EchoServer] Client connected: " + clientSocket.getRemoteSocketAddress());
            
            // Create input and output streams
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            String inputLine;
            // Read messages from client until client disconnects
            while ((inputLine = in.readLine()) != null) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                System.out.println("[" + timestamp + "] Client: " + inputLine);
                
                // Echo the message back to client
                String response = "Echo: " + inputLine;
                out.println(response);
                System.out.println("[" + timestamp + "] Server: " + response);
                
                // Exit if client sends "bye"
                if ("bye".equalsIgnoreCase(inputLine.trim())) {
                    System.out.println("[EchoServer] Client requested disconnect");
                    break;
                }
            }
            
            // Close connection
            clientSocket.close();
            System.out.println("[EchoServer] Client disconnected");
            System.out.println("[EchoServer] Server shutting down");
            
        } catch (IOException e) {
            System.err.println("[EchoServer] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

