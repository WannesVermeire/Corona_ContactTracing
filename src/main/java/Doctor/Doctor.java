package Doctor;

import Visitor.Visitor;
import Visitor.Visit;

import java.io.File;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static Services.Methods.*;

public class Doctor {
    private String name;
    private KeyPair keyPair;
    private List<Visit> visits;
    private ArrayList<List<byte[]>> signedLogs;

    public Doctor(String name) {
        this.name = name;
        this.keyPair = getKeyPair();
        visits = new ArrayList<>();
    }

    public PublicKey getPublicKey(){
        return keyPair.getPublic();
    }
    public ArrayList<List<byte[]>> getSignedLogs() {
        return signedLogs;
    }

    /******************************** 3. REGISTERING INFECTED USER **********************************/
    public void importVisits() {
        try {
            File logFile = new File("src/main/java/Visitor/visitLogs.txt");
            Scanner myReader = new Scanner(logFile);
            while (myReader.hasNextLine()) {
                String visitString = myReader.nextLine();
                Visit v = new Visit(visitString);
                visits.add(v);
            }
            System.out.println("Visitlogs successfully read: "+visits);
        } catch (Exception e) {e.printStackTrace();}
    }
    // Convert logs to String to byte[] and sign them
    public void generateSignedLogs(){
        signedLogs = new ArrayList<>();
        for (Visit visit : visits) {
            String visitString = visit.getLogString(); // visit = log!!
            byte[] log = stringToBytes(visitString);
            List<byte[]> signaturePair = getSignature(log, keyPair.getPrivate());
            signedLogs.add(signaturePair);
        }
    }


    /******************************** 3. REGISTERING INFECTED USER **********************************/
}
