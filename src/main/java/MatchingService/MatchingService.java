package MatchingService;

import Interfaces.EnrollmentInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
/**
 *  Keeps information about visits and supports contact tracing
 *  Does not contain data to uniquely identify users and catering facilities
 */

public class MatchingService {
    public static void main(String[] args) {
        MatchingServiceDB matchingServiceDB = new MatchingServiceDB();

        // Own server we are hosting
        try {
            Registry registry = LocateRegistry.createRegistry(2300);
            registry.rebind("MatchingService", new MatchingServiceInterfaceImplementation(matchingServiceDB));
        }
        catch (Exception e) { e.printStackTrace(); }
        System.out.println("MatchingService is running");


        /******************************** 3. REGISTERING INFECTED USER **********************************/
        // Try to connect to a different server ourselves
        try {
            // fire to localhost port 2100
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);
            // search for RegistrarService
            EnrollmentInterface impl = (EnrollmentInterface) myRegistry.lookup("RegistrarService");

            //matchingServiceDB.

        } catch (Exception e) { e.printStackTrace(); }
        /******************************** 3. REGISTERING INFECTED USER **********************************/

    }
}
