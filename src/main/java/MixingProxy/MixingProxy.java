package MixingProxy;

import Interfaces.MatchingServiceInterface;
import MatchingService.MatchingServiceInterfaceImplementation;
import Registrar.EnrollmentInterfaceImpl;
import Registrar.RegistrarDB;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Shuffles incoming messages
 * Flushes them at regular times to the matching service
 */

public class MixingProxy {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 2300);
            MatchingServiceInterface impl = (MatchingServiceInterface) registry.lookup("MatchingService");
            // create on port 2101
            registry = LocateRegistry.createRegistry(2200);
            // create a new service named MixingProxyService
            registry.rebind("MixingProxyService", new MixingProxyInterfaceImplementation(impl));
        }
        catch (Exception e) { e.printStackTrace(); }
        System.out.println("Mixing Proxy is ready");
    }
}
