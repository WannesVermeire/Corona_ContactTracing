package Visitor;

import Services.Methods;
import com.google.zxing.NotFoundException;

import java.io.IOException;
import java.io.Serializable;
import java.security.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import static Services.Methods.getKeyPair;

public class Visitor implements Serializable {

    private String name;
    private String phone;
    private List<byte[]>[] signedTokens = new List[31];
    private List<byte[]>[] unsignedTokens = new List[31];
    // Value: {R_i, CF, Hash}
    private Map<String, String[]> visits;

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
    public void setSignedTokens(List<byte[]>[] signedTokens) {
        this.signedTokens = (signedTokens);
    }
    public void setUnsignedTokens(List<byte[]>[] unsignedTokens) {
        this.unsignedTokens = (unsignedTokens);
    }
    public void addVisit(String [] log) {
        visits.put(log[3],log);
    }
    public List<byte[]> getTokens(int day) {
        return signedTokens[day-1];
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

    public byte[] getAndRemoveSignedToken(int today) {
        List<byte[]> tokensToday = signedTokens[today-1];
        byte[] ret_tokens = tokensToday.get(0);
        tokensToday.remove(0);
        signedTokens[today-1] = tokensToday;
        return ret_tokens;
    }
    public byte[] getAndRemoveUnsignedToken(int today) {
        List<byte[]> tokensToday = unsignedTokens[today-1];
        byte[] ret_tokens = tokensToday.get(0);
        tokensToday.remove(0);
        unsignedTokens[today-1] = tokensToday;
        return ret_tokens;
    }
}
