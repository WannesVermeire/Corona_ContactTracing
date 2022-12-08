package Facility;

import Interfaces.EnrollmentInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.PublicKey;
import java.util.ArrayList;


public class FacilityClient {
    public static void main(String[] args) {

        Facility facility = new Facility("Hamann", "Vantegemstraat 3, 9230 Wetteren", "+32 9 333 77 77");
        Facility facility2 = new Facility("Café Hash", "Hashstraat 54, 9230 Wetteren", "32 9 475 13 20");
        // Connect to Registrar server
        try {
            // fire to localhost port 2100
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);
            // search for RegistrarService
            EnrollmentInterface impl = (EnrollmentInterface) myRegistry.lookup("RegistrarService");

            /************************************* 1.1 FACILITY ENROLLMENT *************************************/
            String CF = facility.getCF();
            PublicKey publicKey = facility.getPublicKey();
            ArrayList<byte[]> signaturePair = facility.generateCFWithSignature();

            impl.registerFacility(CF, signaturePair, publicKey);
            facility.setKeyArray(impl.getKeyArray(facility.getId()));
            facility.setNymArray(impl.getNymArray(facility.getId()));

            String CF2 = facility2.getCF();
            PublicKey publicKey2 = facility2.getPublicKey();
            ArrayList<byte[]> signaturePair2 = facility2.generateCFWithSignature();

            impl.registerFacility(CF2, signaturePair2, publicKey2);

            System.out.println("Facility data after enrollment: "+facility);
            /************************************* 1.1 FACILITY ENROLLMENT *************************************/

        } catch (Exception e) { e.printStackTrace(); }

        facility.generateRandoms();
        facility.calculateQRCodes();

    }
}
