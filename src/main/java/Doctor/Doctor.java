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

    public Doctor(String name) {
        this.name = name;
        this.keyPair = getKeyPair();
        visits = new ArrayList<>();
    }
    public void importVisits() {
        try {
            File logFile = new File("src/main/java/Visitor/visitLogs.txt");
            Scanner myReader = new Scanner(logFile);
            while (myReader.hasNextLine()) {
                String visitString = myReader.nextLine();
                Visit v = new Visit(visitString);
                visits.add(v);

                System.out.println("Visitlogs succesfully read: "+visits);
            }
        } catch (Exception e) {e.printStackTrace();}
    }


    public PublicKey getPublicKey (){
        return keyPair.getPublic();
    }

    public ArrayList<List<byte[]>> getSignedLogs(Visitor visitor){
        // TODO link tussen token en visit
        ArrayList<byte[]>[] tokens = visitor.getUsedTokens();
        Map<String, Visit> visits  = visitor.getVisits();
        ArrayList<List<byte[]>> signedLogs = new ArrayList<>();
        int i = 0;
        for (Visit visit : visits.values()) {

            ArrayList<byte[]> token = tokens[i]; //Signed token Todo: is the data alone enough?
            String H = visit.getH();
            String R_i = visit.getR_i();

            //2: Get time intervals that were stored on the smartphone
            String timeInterval = visit.getScanTime();
            String[] stringToken = separateString(bytesToString(token.get(0))); //String manipulations to fix the unfixable
            String[] data = new String[] {stringToken[0],stringToken[1],H,R_i, timeInterval};
            String joinedData = joinStrings(data);

            //3: Sign the tokens + intervals
            ArrayList<byte[]> signature = getSignature(stringToBytes(joinedData),keyPair.getPrivate());
            signedLogs.add(signature);

            i++;
        }
        return signedLogs;
    }
}
