package Visitor;

import Interfaces.EnrollmentInterface;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import java.io.FileInputStream;
import javax.imageio.ImageIO;

import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;


public class VisitorClient {
    public static void main(String[] args) throws NotFoundException, IOException {
        Visitor visitor = new Visitor("Wannes", "+32 456 30 81 66");

        try {
            // fire to localhost port 2100
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);

            // search for RegistrarService
            EnrollmentInterface impl = (EnrollmentInterface) myRegistry.lookup("RegistrarService");

            // call server methods
            impl.registerVisitor(visitor);

            System.out.println("Succesfully registered to the system");

            String H = scanQR(visitor);


            // todo: time intervals... Die snap ik nie goe (Denk dat we ook nog gaan moeten kijken om de incubation time achtig bij te houden)
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            // todo: Ik twijfel nog doordeze functie aan mijn implementatie van de tokens...
            // De twijfel : bij user enrollement - moet ek voor elke dag van de maand nen set vna 48 tokens voorzien? of worden deze per dag uitgedeeld...
            // Dus zou kunnen dat daar nog nen fout in zit...
            impl.sendCapsule(visitor.getToken(day));
        }
        catch (Exception e) { e.printStackTrace(); }
    }
    public static String scanQR(Visitor visitor) throws NotFoundException, IOException {
        // Read QR
        // For now: just dummy procedure where it selects current day
        int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String filename = "QRCodes/QRCode_day" + 3 + ".jpg";
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
        // Store locally
        // todo: is het oke om als key te nemen, day of month?
        visitor.addVisit(new String[]{R_i, CF}, dayOfMonth);
        // Print result log
        System.out.println("Random number: "+R_i);
        System.out.println("Unique Identifier: "+ CF);
        System.out.println("Hash: "+ H);
        System.out.println("Current date & time: "+ currentTime);
        // todo: save and determine for how long this will be saved
        return null;
    }
    public static String readQRcode(String file) throws FileNotFoundException, IOException, NotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedImageLuminanceSource bufferedImageLuminanceSource = new BufferedImageLuminanceSource(ImageIO.read(fileInputStream));
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer((bufferedImageLuminanceSource)));

        return (new MultiFormatReader().decode(binaryBitmap)).getText();
    }

}
