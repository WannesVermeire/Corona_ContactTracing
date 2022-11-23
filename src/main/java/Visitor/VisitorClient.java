package Visitor;

import Interfaces.EnrollmentInterface;
import Interfaces.MixingProxyInterface;
import MixingProxy.Capsule;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import java.io.FileInputStream;
import java.util.List;
import javax.imageio.ImageIO;

import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;


public class VisitorClient {

    public static void main(String[] args) throws NotFoundException, IOException {
        Visitor visitor = new Visitor("Wannes", "+32 456 30 81 66");
        int saveDuration; // Depends on the governmental directives and incubation time
        int incubation;
        ArrayList<FacilityScanData> visits = new ArrayList<>(); //Stores 3 values(R_i,CF,H) exposed by the QR code for each facility visit
        try {
            // https://docs.oracle.com/javase/8/docs/technotes/guides/rmi/hello/hello-world.html
            // myRegistry is a reference (stub) for the registry that is running on port 2100 a.k.a. the Registrar
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);

            // Obtain the stub for the remote object with name "RegistrarService" a.k.a. the EnrollmentInterfaceImpl
            EnrollmentInterface impl = (EnrollmentInterface) myRegistry.lookup("RegistrarService");
            incubation = impl.getINCUBATION_DAYS();

            // Register visitor to the registrar and get a set of signed tokens
            visitor = impl.registerVisitor(visitor);

            System.out.println("Succesfully registered to the system");

            /** 2.1: scanQR **/
            // visitor scans a QR code
            FacilityScanData fs = scanQR(visitor);
            // simplify saveDuration by setting it to the incubation time
            saveDuration = incubation;
            visits.add(fs);

            // mixingProxyRegistry is a reference (stub) for the registry that is running on port 2200 a.k.a. the MixingProxy
            Registry mixingProxyRegistry = LocateRegistry.getRegistry("localhost", 2200);
            // Obtain the stub for the remote object with name "MixingProxyService" a.k.a. the MixingProxyInterfaceImplementation
            MixingProxyInterface mpi = (MixingProxyInterface) mixingProxyRegistry.lookup("MixingProxyService");

            // For this implementation we choose half an hour for the time intervals(24h/48)
            // For each time interval there's a unique token per day
            int currentTimeInterval = 2*((fs.getScanTime().charAt(11)-48)*10 + fs.getScanTime().charAt(12)-48) + (fs.getScanTime().charAt(14)-48)/3;
            int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            List<byte[]> tokens = visitor.getTokens(today); //NullpointerException
            byte[] token = tokens.get(currentTimeInterval);
            // todo: secure connection with server authentication
            //List<byte[]> tokens = new ArrayList<>(visitor.getTokens(day));

            //Calendar current_dateTime = fs.getScanDay();*/
            //System.out.println(time_interval);

            /** 2.2 : sendCapsule **/
            Capsule capsule = new Capsule(currentTimeInterval, token, fs.getH());
            mpi.sendCapsule(visitor, capsule);
            tokens.remove(0);
        }
        catch (Exception e) { e.printStackTrace(); }
    }
    public static FacilityScanData scanQR(Visitor visitor) throws NotFoundException, IOException {
        // Read QR
        // For now: just dummy procedure where it selects current day
        Calendar current_dateTime = Calendar.getInstance();
        String filename = "QRCodes/QRCode_day" + current_dateTime.get(Calendar.DAY_OF_MONTH) + ".jpg";
        String[] qr = readQRcode(filename).split(";");
        // random number
        String R_i = qr[0];
        // Unique identifier of the facility
        String CF = qr[1];
        // Hash
        String H = qr[2];
        // Get Current time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String currentTime = dtf.format(LocalDateTime.now());
        // todo: Store locally
        visitor.addVisit(new String[]{R_i, CF, H, currentTime});
        // Print result log
        System.out.println("Random number: "+R_i);
        System.out.println("Unique Identifier: "+ CF);
        System.out.println("Hash: "+ H);
        System.out.println("Entry time: " + currentTime);
        return new FacilityScanData(R_i, CF, H, currentTime);
    }
    public static String readQRcode(String file) throws FileNotFoundException, IOException, NotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedImageLuminanceSource bufferedImageLuminanceSource = new BufferedImageLuminanceSource(ImageIO.read(fileInputStream));
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer((bufferedImageLuminanceSource)));

        return (new MultiFormatReader().decode(binaryBitmap)).getText();
    }

}
