package MatchingService;

import Interfaces.RegistrarInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeUnit;

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
            registry.rebind("MatchingService", new MatchingServiceInterfaceImpl(matchingServiceDB));
        }
        catch (Exception e) { e.printStackTrace(); }
        System.out.println("MatchingService is running");

    }

    // Aparte methode om te kunnen oproepen in globalMain
    public static void getNyms() {
        /******************************** 3. REGISTERING INFECTED USER **********************************/
        // Try to connect to a different server ourselves
        try {
            TimeUnit.SECONDS.sleep(10); // wachten tot alle voorgaande stappen voltooid zijn
            // fire to localhost port 2100
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);
            // search for RegistrarService
            RegistrarInterface impl = (RegistrarInterface) myRegistry.lookup("RegistrarService");
            // Download all nym from the registrar
            matchingServiceDB.addNym(impl.getAllNym());

        } catch (Exception e) { e.printStackTrace(); }
        /******************************** 3. REGISTERING INFECTED USER **********************************/
    }
}
