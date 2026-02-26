package rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * GreetingServer - Enhanced with Network Deployment Support (Week 10)
 * 
 * Enhanced features:
 * - Supports network deployment with IP address parameter
 * - Configurable port (default 1099)
 * - Better error handling and troubleshooting guidance
 * 
 * Usage:
 *   Local:  java rmi.GreetingServer
 *   Network: java rmi.GreetingServer 192.168.1.100 1099
 */
public class GreetingServer {
    
    private static final int DEFAULT_PORT = 1099;
    private static final String DEFAULT_HOST = "localhost";
    private static final String SERVICE_NAME = "GreetingService";
    
    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        
        // Parse command-line arguments for network deployment
        if (args.length >= 1) {
            host = args[0];
            System.out.println("[GreetingServer] Using host: " + host);
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
                System.out.println("[GreetingServer] Using port: " + port);
            } catch (NumberFormatException e) {
                System.err.println("[GreetingServer] Invalid port, using default: " + DEFAULT_PORT);
            }
        }
        
        try {
            // Start RMI registry on specified port
            try {
                LocateRegistry.createRegistry(port);
                System.out.println("[GreetingServer] RMI registry started on port " + port);
            } catch (Exception e) {
                // Registry may already exist, try to get it
                LocateRegistry.getRegistry(port);
                System.out.println("[GreetingServer] Using existing RMI Registry on port " + port);
            }
            
            // Create service implementation
            GreetingServiceImpl greeter = new GreetingServiceImpl();
            
            // Bind service to registry with network support
            String serviceUrl = "rmi://" + host + ":" + port + "/" + SERVICE_NAME;
            Naming.rebind(serviceUrl, greeter);
            System.out.println("[GreetingServer] GreetingService bound to: " + serviceUrl);
            System.out.println("[GreetingServer] Server is ready and waiting for client calls...");
            
            if (!host.equals("localhost") && !host.equals("127.0.0.1")) {
                System.out.println("[GreetingServer] Network deployment mode - ensure firewall allows port " + port);
            }
            
        } catch (Exception e) {
            System.err.println("[GreetingServer] Error starting server: " + e.getMessage());
            e.printStackTrace();
            System.err.println("\n[GreetingServer] Troubleshooting:");
            System.err.println("1. Ensure port " + port + " is not already in use");
            System.err.println("2. For network deployment, check firewall settings");
            System.err.println("3. Verify host IP address is correct");
            System.exit(1);
        }
    }
}
