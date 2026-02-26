package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * EchoClient - Socket Client for Echo Server (Week 11)
 * 
 * Demonstrates:
 * - Client socket connection to server
 * - Sending messages to server
 * - Receiving responses from server
 * - Input/Output stream usage
 * 
 * Usage:
 *   java network.EchoClient [host] [port]
 *   Default host: localhost
 *   Default port: 8888
 * 
 * Instructions:
 * - Type messages and press Enter to send
 * - Type "bye" to disconnect
 */
public class EchoClient {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8888;
    
    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port, using default: " + DEFAULT_PORT);
            }
        }
        
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {
            
            System.out.println("[EchoClient] Connected to server: " + host + ":" + port);
            System.out.println("[EchoClient] Type messages (type 'bye' to exit):");
            System.out.println();
            
            // Thread to read messages from server
            Thread readerThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println("[Server] " + response);
                    }
                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        System.err.println("[EchoClient] Error reading from server: " + e.getMessage());
                    }
                }
            });
            readerThread.start();
            
            // Main thread: read user input and send to server
            String userInput;
            while (true) {
                System.out.print("[You] ");
                userInput = scanner.nextLine();
                
                if (userInput == null || userInput.trim().isEmpty()) {
                    continue;
                }
                
                // Send message to server
                out.println(userInput);
                
                // Exit if user types "bye"
                if ("bye".equalsIgnoreCase(userInput.trim())) {
                    System.out.println("[EchoClient] Disconnecting...");
                    break;
                }
            }
            
            // Close socket (will cause reader thread to exit)
            socket.close();
            System.out.println("[EchoClient] Disconnected from server");
            
        } catch (IOException e) {
            System.err.println("[EchoClient] Error: " + e.getMessage());
            System.err.println("\nTroubleshooting:");
            System.err.println("1. Ensure server is running: java network.EchoServer [port]");
            System.err.println("2. Check host and port are correct");
            System.err.println("3. Verify firewall allows connection");
            e.printStackTrace();
        }
    }
}

