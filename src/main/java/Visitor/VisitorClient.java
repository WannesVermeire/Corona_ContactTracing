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
        int incubation;
        ArrayList<FacilityScanData> visits = new ArrayList<>();
        try {
            // fire to localhost port 2100
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);

            // search for RegistrarService
            EnrollmentInterface impl = (EnrollmentInterface) myRegistry.lookup("RegistrarService");
            incubation = impl.getINCUBATION_DAYS();

            // call server methods
            visitor = impl.registerVisitor(visitor);

            System.out.println("Succesfully registered to the system");

            /** 2.1: scanQR **/
            // visitor scans a QR code
            FacilityScanData fs = scanQR(visitor);
            // todo: Determine for how long this will be saved (incubation period)
            visits.add(fs);

            // search for MixingProxyServer
            Registry mixingProxyRegistry = LocateRegistry.getRegistry("localhost", 2200);
            MixingProxyInterface mpi = (MixingProxyInterface) mixingProxyRegistry.lookup("MixingProxyService");

            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            // todo: secure connection with server authentication
            List<byte[]> tokens = new ArrayList<>(visitor.getTokens(day));

            Calendar current_dateTime = fs.getScanDay();
            //System.out.println(time_interval);

            /** 2.2 : sendCapsule **/
            Capsule capsule = new Capsule(current_dateTime, tokens.get(0), fs.getH());
            mpi.sendCapsule(visitor, capsule, capsule.getToken());
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
        // Store locally
        // todo: is het oke om als key te nemen, day of month?
        visitor.addVisit(new String[]{R_i, CF}, Calendar.DAY_OF_MONTH);
        // Print result log
        System.out.println("Random number: "+R_i);
        System.out.println("Unique Identifier: "+ CF);
        System.out.println("Hash: "+ H);
        return new FacilityScanData(R_i, CF, H, current_dateTime);
    }
    public static String readQRcode(String file) throws FileNotFoundException, IOException, NotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedImageLuminanceSource bufferedImageLuminanceSource = new BufferedImageLuminanceSource(ImageIO.read(fileInputStream));
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer((bufferedImageLuminanceSource)));

        return (new MultiFormatReader().decode(binaryBitmap)).getText();
    }

}
