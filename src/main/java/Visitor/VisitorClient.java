package Visitor;

import Interfaces.EnrollmentInterface;
import Interfaces.MixingProxyInterface;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Calendar;

import java.io.FileInputStream;
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
        try {
            // https://docs.oracle.com/javase/8/docs/technotes/guides/rmi/hello/hello-world.html
            // myRegistry is a reference (stub) for the registry that is running on port 2100 a.k.a. the Registrar
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);

            /*********************************** USER ENROLMENT *************************************/
            // Obtain the stub for the remote object with name "RegistrarService" a.k.a. the EnrollmentInterfaceImpl
            EnrollmentInterface impl = (EnrollmentInterface) myRegistry.lookup("RegistrarService");
            incubation = impl.getINCUBATION_DAYS();

            // Register visitor to the registrar and get a set of signed tokens
            visitor = impl.registerVisitor(visitor);

            System.out.println("Succesfully registered to the system");
            /*********************************** USER ENROLMENT *************************************/



            /*********************************** VISITING FACILITY *************************************/
            // visitor scans a QR code
            FacilityScanData fs = visitor.scanQR();
            // simplify saveDuration by setting it to the incubation time
            saveDuration = incubation;



            // mixingProxyRegistry is a reference (stub) for the registry that is running on port 2200 a.k.a. the MixingProxy
            Registry mixingProxyRegistry = LocateRegistry.getRegistry("localhost", 2200);
            // Obtain the stub for the remote object with name "MixingProxyService" a.k.a. the MixingProxyInterfaceImplementation
            MixingProxyInterface mpi = (MixingProxyInterface) mixingProxyRegistry.lookup("MixingProxyService");

            // For this implementation we choose half an hour for the time intervals(24h/48)
            // For each time interval there's a unique token per day

            int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            ArrayList<byte[]> verifiedTokens = mpi.verifyAndSignCapsule(visitor, impl.getPublicKey(), fs.getScanTime(), visitor.getAndRemoveToken(today), stringToBytes(fs.getH()));
            /*********************************** VISITING FACILITY *************************************/

        } catch (Exception e) { e.printStackTrace(); }


    }

    public static String readQRcode(String file) throws FileNotFoundException, IOException, NotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedImageLuminanceSource bufferedImageLuminanceSource = new BufferedImageLuminanceSource(ImageIO.read(fileInputStream));
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer((bufferedImageLuminanceSource)));

        return (new MultiFormatReader().decode(binaryBitmap)).getText();
    }

}
