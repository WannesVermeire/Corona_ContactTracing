package MatchingService;

import Interfaces.RegistrarInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeUnit;

import static Services.Methods.connectToRegistrar;

/**
 *  Keeps information about visits and supports contact tracing
 *  Does not contain data to uniquely identify users and catering facilities
 */

public class MatchingService {
    static MatchingServiceDB matchingServiceDB = new MatchingServiceDB();

    public static void main(String[] args) {

        // Own server we are hosting
        try {
            Registry registry = LocateRegistry.createRegistry(2300);
            registry.rebind("MatchingService", new MatchingServiceGUI(matchingServiceDB));
        }
        catch (Exception e) { e.printStackTrace(); }
        System.out.println("MatchingService is running");
    }
}
