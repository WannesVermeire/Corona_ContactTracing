package Facility;

import Interfaces.EnrollmentInterface;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;

public class BarOwnerClient {
    public static void main(String[] args) {

        /** Phase 1.1: Enrollment **/
        //TODO: is het de bedoelign dat de BarOwner zijn eigen id kiest? Is het niet de bedoeling dat hij ene krijgt @Wannes
        // wnr ge et leest moedet maar sturen op discord jong
        // Mvg, Wout
        Facility facility = new Facility(1234, "Hamann", "Vantegemstraat 3, 9230 Wetteren", "+32 9 333 77 77");

        // Register to Registrar server
        try {
            // fire to localhost port 2100
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);

            // search for RegistrarService
            EnrollmentInterface impl = (EnrollmentInterface) myRegistry.lookup("RegistrarService");

            // call server methods
            impl.registerFacility(facility);
            facility.setNymArray(impl.getNymArray(facility.getId()));


            System.out.println(facility);
        } catch (Exception e) { e.printStackTrace(); }

        // Calculate QR-codes based on nymArray
        List<byte[]> nymArray = facility.getNymArray();
        for (int i=0; i<nymArray.size(); i++) {

            // Create hash of R_i and nym_CF_dayi
            byte[] hash = null;
            byte[] nym = nymArray.get(i);
            byte[] random = new byte[20];
            new Random().nextBytes(random);
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] data = ArrayUtils.addAll(random, nym);
                hash = digest.digest(data);
            }
            catch (Exception e) { e.printStackTrace(); }

            // Create content of QR code
            String randomString = Base64.getEncoder().encodeToString(random);
            String CFString = facility.getCF();
            String hashString = Base64.getEncoder().encodeToString(hash);

            String barcodeText = randomString+CFString+hashString;

            // TODO ter info voor decode
//            byte[] decode = Base64.getDecoder().decode(s);

            // Create QR code
            String filename = "QRCodes/QRCode_day"+(i+1)+".jpg";
            try {
                QRCodeWriter barcodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);

                BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
                File outputfile = new File(filename);
                ImageIO.write(bufferedImage, "jpg", outputfile);

            } catch (Exception e) { e.printStackTrace(); }

        }








    }
}
