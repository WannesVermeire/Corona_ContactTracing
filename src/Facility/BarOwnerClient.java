package Facility;

import Interfaces.EnrollmentInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BarOwnerClient {
    public static void main(String[] args) {
        /** Phase 1.1: Enrollment **/
        Facility facility = new Facility(1234, "Hamann", "Vantegemstraat 3, 9230 Wetteren", "+32 9 333 77 77");

        try {

            // fire to localhost port 2100
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);

            // search for CounterService
            EnrollmentInterface impl = (EnrollmentInterface) myRegistry.lookup("RegistrarService");

            // call server's method
            impl.registerFacility(facility.getCF());

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
