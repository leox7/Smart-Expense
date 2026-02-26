package rmi;

import java.rmi.Naming;

/**
 * GreetingClient - Enhanced with Network Connection Support (Week 10)
 * 
 * Enhanced features:
 * - Supports connecting to remote servers using IP address
 * - Configurable host and port
 * - Better error handling
 * 
 * Usage:
 *   Local:  java rmi.GreetingClient
 *   Remote: java rmi.GreetingClient 192.168.1.100 1099
 */
public class GreetingClient {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1099;
    private static final String SERVICE_NAME = "GreetingService";
    
    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        
        // Parse command-line arguments for remote connection
        if (args.length >= 1) {
            host = args[0];
            System.out.println("[GreetingClient] Connecting to host: " + host);
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("[GreetingClient] Invalid port, using default: " + DEFAULT_PORT);
            }
        }
        
        try {
            // Construct service URL for network connection
            String serviceUrl = "rmi://" + host + ":" + port + "/" + SERVICE_NAME;
            System.out.println("[GreetingClient] Looking up service at: " + serviceUrl);
            
            // Look up remote service
            GreetingService greeter = (GreetingService) Naming.lookup(serviceUrl);
            System.out.println("[GreetingClient] Successfully connected to GreetingService!");
            
            // Call remote method
            String reply = greeter.sayHello("SmartExpense User");
            System.out.println("[GreetingClient] Received response: " + reply);
            
        } catch (Exception e) {
            System.err.println("[GreetingClient] Error connecting to server: " + e.getMessage());
            e.printStackTrace();
            System.err.println("\n[GreetingClient] Troubleshooting:");
            System.err.println("1. Ensure server is running: java rmi.GreetingServer [host] [port]");
            System.err.println("2. Check firewall settings on server");
            System.err.println("3. Verify host and port are correct");
            System.exit(1);
        }
    }
}
