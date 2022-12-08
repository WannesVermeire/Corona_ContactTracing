package Visitor;

import Doctor.Doctor;
import Interfaces.EnrollmentInterface;
import Interfaces.MatchingServiceInterface;
import Interfaces.MixingProxyInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static Services.Methods.*;


public class VisitorClient {

    public static void main(String[] args) {
        Visitor visitor = new Visitor("Wannes", "+32 456 30 81 66");
        int saveDuration = 14; // Days before we delete the capsules from visiting a facility
        int incubation = 0;
        PublicKey publicKeyRegistrar = null;

        /************************************** 1.2 USER ENROLLMENT *************************************/
        try {
            // fire to localhost port 2100
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);
            // search for RegistrarService
            EnrollmentInterface impl = (EnrollmentInterface) myRegistry.lookup("RegistrarService");

            incubation = impl.getINCUBATION_DAYS();

            // Register visitor to the registrar
            boolean registrationSuccessful = impl.registerVisitor(visitor.getName(), visitor.getPhoneNr());
            if (registrationSuccessful) System.out.println("Visitor data after enrollment: "+visitor);
            else System.out.println("Something went wrong during enrollment of: "+visitor);

            // Get a set of signed tokens
            visitor.setTokens(impl.getSignedTokens(visitor.getPhoneNr()));
            System.out.println("Visitor data after receiving tokens: "+visitor);

            // Key needed in 2. Visiting a facility
            publicKeyRegistrar = impl.getPublicKey();
        } catch (Exception e) { e.printStackTrace(); }
        /************************************* 1.2 USER ENROLLMENT **************************************/


        /*********************************** 2. VISITING A FACILITY *************************************/
        try {
            // visitor scans a QR code
            Visit visit = visitor.scanQR();

            // fire to localhost port 2200
            Registry mixingProxyRegistry = LocateRegistry.getRegistry("localhost", 2200);
            // search for MixingProxyService
            MixingProxyInterface mpi = (MixingProxyInterface) mixingProxyRegistry.lookup("MixingProxyService");

            // Create a capsule and send it to the MixingProxy to verify
            // Capsule = timestamp, T_user_x_dayi, hash(Ri,num_CF_dayi) (hash uit de QR-code dus)
            int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            ArrayList<byte[]> signedConfirmation = mpi.verifyAndSendConfirmation(visitor, publicKeyRegistrar, visit.getScanTime(), visitor.getAndRemoveToken(today), stringToBytes(visit.getH()));
            Visualiser visualiser = new Visualiser(signedConfirmation.get(1));

        } catch (Exception e) { e.printStackTrace(); }
        /*********************************** 2. VISITING A FACILITY *************************************/


        /******************************** 3. REGISTERING INFECTED USER **********************************/
        try {
            // Write visits to file
            visitor.exportVisits();


            // fire to localhost port 2100
            Registry matchingRegistry = LocateRegistry.getRegistry("localhost", 2300);
            // search for RegistrarService
            MatchingServiceInterface msi = (MatchingServiceInterface) matchingRegistry.lookup("MatchingService");

            // Visitor gets infected and goes to a doctor
            Doctor doctor = new Doctor("Eeraerts Toon");

            // Doctor follows the procedure for when a visitor gets infected
            ArrayList<List<byte[]>> signedLogs = doctor.getSignedLogs(visitor);

            //Doctor sends the data to the matching service
//            msi.forwardSickPatientData(signedLogs, doctor.getPublicKey());
        } catch (Exception e) { e.printStackTrace(); }
        /******************************** 3. REGISTERING INFECTED USER **********************************/

    }
}
