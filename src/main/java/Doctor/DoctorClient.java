//package Doctor;
//
//
//import Interfaces.MatchingServiceInterface;
//
//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
//
//public class DoctorClient {
//    public static void main(String[] args) {
//
//        /******************************** 3. REGISTERING INFECTED USER **********************************/
//        Doctor doctor = new Doctor("Toon Eeraerts");
//        doctor.importVisits();
//
//        // Connect to Matching service
//        try {
//            // fire to localhost port 2300
//            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2300);
//            // search for RegistrarService
//            MatchingServiceInterface impl = (MatchingServiceInterface) myRegistry.lookup("MatchingService");
//
//            // Send signedLogs to the server
//            doctor.generateSignedLogs();
//            impl.receiveSignedLogs(doctor.getSignedLogs(), doctor.getPublicKey());
//
//        } catch (Exception e) { e.printStackTrace(); }
//
//
//        /******************************** 3. REGISTERING INFECTED USER **********************************/
//    }
//}
