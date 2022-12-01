package Visitor;

import Services.Methods;
import com.google.zxing.NotFoundException;

import java.io.IOException;
import java.io.Serializable;
import java.security.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static Services.Methods.*;

public class Visitor implements Serializable {

    private String name;
    private String phone;

    private Map<String, String[]> visits;

    SignedTokenList tokens;
    ArrayList<byte[]>[] usedTokens;
    private KeyPair keyPair;

    public Visitor(String name, String phone) {
        this.name = name;
        this.phone = phone;
        visits = new HashMap();
        this.keyPair = getKeyPair();
        usedTokens =  null;
    }

    public String getName() { return name; }
    public String getPhone() {
        return phone;
    }
    public void setTokens(SignedTokenList tokens) {
        this.tokens = tokens;
    }
    public void addVisit(String [] log) {
        System.out.println("test :" + log[3]);
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
            String currentTime = timeStampToString(LocalDateTime.now());
            addVisit(new String[]{R_i, CF, H, currentTime});
            // Print result log
            System.out.println("Random number: "+R_i);
            System.out.println("Unique Identifier: "+ CF);
            System.out.println("Hash: "+ H);
            System.out.println("Entry time: " + currentTime);
            return new FacilityScanData(R_i, CF, H, currentTime);
    }

    public ArrayList<byte[]> getAndRemoveToken(int today) {
        ArrayList<byte[]> currentTokens = tokens.getAndRemoveSignatureToken(today);

        // Update usedTokens
        if(usedTokens == null) {
            usedTokens = new ArrayList[1];
            usedTokens[0] = currentTokens;
        }
        else {
            ArrayList<byte[]> [] newList = new ArrayList[usedTokens.length+1];
            for(int i=0; i< usedTokens.length; i++) {
                newList[i] = usedTokens[i];
            }
            newList[usedTokens.length] = currentTokens;
            usedTokens = newList;
        }

        return currentTokens;
    }

    public void removeExpirdeVisits(int INCUBATION_DAYS) {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        ArrayList<String> toRemove = new ArrayList<>();
        for(String date : visits.keySet()) {
            if(today > stringToDate(date).getDayOfMonth() + INCUBATION_DAYS) {
                toRemove.add(date);
            }
        }
        for(String date : toRemove) {
            visits.remove(date);
        }
    }
}
