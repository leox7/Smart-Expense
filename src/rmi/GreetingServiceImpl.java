package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GreetingServiceImpl extends UnicastRemoteObject implements GreetingService {
    public GreetingServiceImpl() throws RemoteException {
        super();
    }
    @Override
    public String sayHello(String name) throws RemoteException {
        System.out.println("[Server] Received request for greeting: " + name);
        return "Hello from RMI, " + name + "!";
    }
}
