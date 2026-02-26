package rmi;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for GreetingService RMI demo (Week 9)
 */
public interface GreetingService extends Remote {
    String sayHello(String name) throws RemoteException;
}
