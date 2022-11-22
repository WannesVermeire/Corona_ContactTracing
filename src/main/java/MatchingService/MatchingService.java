package MatchingService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
/**
 *  Keeps information about visits and supports contact tracing
 *  Does not contain data to uniquely identify users and catering facilities
 */

public class MatchingService {
    public static void main(String[] args) {
        MatchingServiceDB matchingServiceDB = new MatchingServiceDB();

        try {
            Registry registry = LocateRegistry.createRegistry(2300);
            registry.rebind("MatchingService", new MatchingServiceInterfaceImplementation(matchingServiceDB));
        }
        catch (Exception e) { e.printStackTrace(); }
        System.out.println("MatchingService is running");

    }
}
