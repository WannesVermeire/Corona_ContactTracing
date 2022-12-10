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

    // Aparte methode om te kunnen oproepen in globalMain
    public static void getNyms() {
        /******************************** 3. REGISTERING INFECTED USER **********************************/
        // Try to connect to a different server ourselves
        try {
            RegistrarInterface impl = connectToRegistrar();
            // Download all nyms from the registrar matching the given CF's
            matchingServiceDB.addNym(impl.getAllNym(matchingServiceDB.getCFFromSignedLogs()));
            matchingServiceDB.verifyLogs();
            matchingServiceDB.generateEntries();
            matchingServiceDB.markInfectedCapsules();
            matchingServiceDB.markInfectedTokens();

        } catch (Exception e) { e.printStackTrace(); }
        /******************************** 3. REGISTERING INFECTED USER **********************************/
    }
}
