package Visitor;

import Services.Methods;
import com.google.zxing.NotFoundException;

import java.io.IOException;
import java.io.Serializable;
import java.security.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static Services.Methods.getKeyPair;

public class Visitor implements Serializable {

    private String name;
    private String phone;

    // Value: {R_i, CF, Hash}
    private Map<String, String[]> visits;

    SignedTokenList tokens;
    private KeyPair keyPair;

    public Visitor(String name, String phone) {
        this.name = name;
        this.phone = phone;
        visits = new HashMap();
        this.keyPair = getKeyPair();
    }

    public String getName() { return name; }
    public String getPhone() {
        return phone;
    }
    public void setTokens(SignedTokenList tokens) {
        this.tokens = tokens;
    }
    public void addVisit(String [] log) {
        visits.put(log[3],log);
    }


    public PublicKey getPublicKey () {
        return keyPair.getPublic();
    }

    public FacilityScanData scanQR()  throws NotFoundException, IOException {
            // Read QR
            // For now: just dummy procedure where it selects current day
            Calendar current_dateTime = Calendar.getInstance();
            String filename = "QRCodes/QRCode_day" + current_dateTime.get(Calendar.DAY_OF_MONTH) + ".jpg";
            String[] qr = Methods.separateString(VisitorClient.readQRcode(filename));
            // random number
            String R_i = qr[0];
            // Unique identifier of the facility
            String CF = qr[1];
            // Hash
            String H = qr[2];
            // Get Current time
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            String currentTime = dtf.format(LocalDateTime.now());
            addVisit(new String[]{R_i, CF, H, currentTime});
            // Print result log
            System.out.println("Random number: "+R_i);
            System.out.println("Unique Identifier: "+ CF);
            System.out.println("Hash: "+ H);
            System.out.println("Entry time: " + currentTime);
            return new FacilityScanData(R_i, CF, H, currentTime);
    }

    public ArrayList<byte[]> getAndRemoveToken(int today) {
        return tokens.getAndRemoveSignatureToken(today);
    }
}
