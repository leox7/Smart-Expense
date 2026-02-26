package rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * ExpenseServer - RMI Server with Network Deployment Support (Week 10)
 * 
 * Features:
 * - Supports localhost and network IP deployment
 * - Configurable port (default 1099)
 * - Proper registry binding for remote access
 * - Firewall and network configuration guidance
 * 
 * Usage:
 *   Local:  java rmi.ExpenseServer
 *   Network: java rmi.ExpenseServer 192.168.1.100 1099
 * 
 * Network Deployment Requirements:
 * 1. Firewall: Open port 1099 (or custom port) in firewall
 * 2. IP Binding: Server must bind to actual IP, not localhost
 * 3. Registry: Must start registry before binding service
 * 4. Security: May require security policy file (see policy/rmi.policy)
 */
public class ExpenseServer {
    
    private static final int DEFAULT_PORT = 1099;
    private static final String DEFAULT_HOST = "localhost";
    private static final String SERVICE_NAME = "ExpenseService";
    
    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        
        // Parse command-line arguments for network deployment
        if (args.length >= 1) {
            host = args[0];
            System.out.println("[ExpenseServer] Using host: " + host);
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
                System.out.println("[ExpenseServer] Using port: " + port);
            } catch (NumberFormatException e) {
                System.err.println("[ExpenseServer] Invalid port, using default: " + DEFAULT_PORT);
            }
        }
        
        try {
            // Set system property for codebase (if serving classes remotely)
            // System.setProperty("java.rmi.server.codebase", "file:/path/to/classes/");
            
            // Start RMI registry on specified port
            // If registry already exists, this will use the existing one
            try {
                LocateRegistry.createRegistry(port);
                System.out.println("[ExpenseServer] RMI Registry created on port " + port);
            } catch (Exception e) {
                // Registry may already exist, try to get it
                LocateRegistry.getRegistry(port);
                System.out.println("[ExpenseServer] Using existing RMI Registry on port " + port);
            }
            
            // Create service implementation
            ExpenseServiceImpl expenseService = new ExpenseServiceImpl();
            
            // Bind service to registry
            // For network deployment, use rmi://host:port/ServiceName
            String serviceUrl;
            if (host.equals("localhost") || host.equals("127.0.0.1")) {
                serviceUrl = "rmi://" + host + ":" + port + "/" + SERVICE_NAME;
            } else {
                // For network deployment, use the actual IP/hostname
                serviceUrl = "rmi://" + host + ":" + port + "/" + SERVICE_NAME;
                System.out.println("[ExpenseServer] Network deployment mode - ensure firewall allows port " + port);
            }
            
            Naming.rebind(serviceUrl, expenseService);
            System.out.println("[ExpenseServer] ExpenseService bound to: " + serviceUrl);
            System.out.println("[ExpenseServer] Server is ready and waiting for client connections...");
            System.out.println("[ExpenseServer] Clients should connect using: " + serviceUrl);
            
            // Keep server running
            System.out.println("[ExpenseServer] Press Ctrl+C to stop the server");
            
        } catch (Exception e) {
            System.err.println("[ExpenseServer] Error starting server: " + e.getMessage());
            e.printStackTrace();
            System.err.println("\n[ExpenseServer] Troubleshooting:");
            System.err.println("1. Ensure port " + port + " is not already in use");
            System.err.println("2. For network deployment, check firewall settings");
            System.err.println("3. Verify host IP address is correct");
            System.err.println("4. Ensure all classes are in classpath");
            System.exit(1);
        }
    }
}

