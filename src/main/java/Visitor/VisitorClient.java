package Visitor;

import Doctor.Doctor;
import Interfaces.EnrollmentInterface;
import Interfaces.MatchingServiceInterface;
import Interfaces.MixingProxyInterface;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import java.io.FileInputStream;
import java.util.Map;
import javax.imageio.ImageIO;

import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import static Services.Methods.*;


public class VisitorClient {

    public static void main(String[] args) throws NotFoundException, IOException {
        Visitor visitor = new Visitor("Wannes", "+32 456 30 81 66");
        int saveDuration; // Depends on the governmental directives and incubation time
        int incubation;

        // Connect to Registrar server
        try {
            // fire to localhost port 2100
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);
            // search for RegistrarService
            EnrollmentInterface impl = (EnrollmentInterface) myRegistry.lookup("RegistrarService");

            /************************************* 1.2 USER ENROLLMENT *************************************/
            incubation = impl.getINCUBATION_DAYS();

            // Register visitor to the registrar
            boolean registrationSuccessful = impl.registerVisitor(visitor.getName(), visitor.getPhoneNr());
            if (registrationSuccessful) System.out.println("Visitor data after enrollment: "+visitor);
            else System.out.println("Something went wrong during enrollment of: "+visitor);

            // Get a set of signed tokens
            visitor.setTokens(impl.getSignedTokens(visitor.getPhoneNr()));
            System.out.println("Visitor data after receiving tokens: "+visitor);
            /************************************* 1.2 USER ENROLLMENT *************************************/



            /*********************************** VISITING FACILITY *************************************/
            // visitor scans a QR code
            FacilityScanData facilityScanData = visitor.scanQR();
            // simplify saveDuration by setting it to the incubation time
            saveDuration = incubation;



            // mixingProxyRegistry is a reference (stub) for the registry that is running on port 2200 a.k.a. the MixingProxy
            Registry mixingProxyRegistry = LocateRegistry.getRegistry("localhost", 2200);
            // Obtain the stub for the remote object with name "MixingProxyService" a.k.a. the MixingProxyInterfaceImplementation
            MixingProxyInterface mpi = (MixingProxyInterface) mixingProxyRegistry.lookup("MixingProxyService");

            // For this implementation we choose half an hour for the time intervals(24h/48)
            // For each time interval there's a unique token per day

            int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            ArrayList<byte[]> verifiedTokens = mpi.verifyAndSignCapsule(visitor, impl.getPublicKey(), facilityScanData.getScanTime(), visitor.getAndRemoveToken(today), stringToBytes(facilityScanData.getH()));
            /*********************************** VISITING FACILITY *************************************/

            /*********************************** REGISTERING INFECTED USER *************************************/

            // matchingRegistry is a reference (stub) for the registry that is running on port 2300 a.k.a. the MatchingService
            Registry matchingRegistry = LocateRegistry.getRegistry("localhost", 2300);
            // Obtain the stub for the remote object with name "MatchingService" a.k.a. the MatchingServiceInterfaceImplementation
            MatchingServiceInterface msi = (MatchingServiceInterface) matchingRegistry.lookup("MatchingService");

            // Visitor gets infected and goes to a doctor
            Doctor doctor = new Doctor("Eeraerts Toon");

            // Doctor follows the procedure for when a visitor gets infected
            String[] signedLogs = doctor.getSignedLogs(visitor);

            //Doctor sends the data to the matching service
//            msi.forwardSickPatientData(signedLogs, doctor.getPublicKey());

        }catch (Exception e) { e.printStackTrace(); }



    }

    public static String readQRcode(String file) throws FileNotFoundException, IOException, NotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedImageLuminanceSource bufferedImageLuminanceSource = new BufferedImageLuminanceSource(ImageIO.read(fileInputStream));
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer((bufferedImageLuminanceSource)));

        return (new MultiFormatReader().decode(binaryBitmap)).getText();
    }

}
