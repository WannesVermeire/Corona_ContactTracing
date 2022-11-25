package Facility;

import Interfaces.EnrollmentInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.PublicKey;


public class BarOwnerClient {
    public static void main(String[] args) {

        /** Phase 1.1: Enrollment **/
        Facility facility = new Facility("Hamann", "Vantegemstraat 3, 9230 Wetteren", "+32 9 333 77 77");

        // Register to Registrar server
        try {
            // fire to localhost port 2100
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);
            // search for RegistrarService
            EnrollmentInterface impl = (EnrollmentInterface) myRegistry.lookup("RegistrarService");
            // call server methods
            String CF = facility.getCF();
            PublicKey publicKey = facility.getPublicKey();
            byte[] signature = facility.generateCFSignature();

            impl.registerFacility(CF, publicKey, signature);
            facility.setKeyArray(impl.getKeyArray(facility.getId()));
            facility.setNymArray(impl.getNymArray(facility.getId()));

            System.out.println(facility);

        } catch (Exception e) { e.printStackTrace(); }

        facility.generateRandoms();
        facility.calculateQRCodes();

    }
}
