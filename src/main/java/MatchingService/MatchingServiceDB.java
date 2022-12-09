package MatchingService;

import Visitor.Visit;

import java.security.PublicKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static Services.Methods.*;

public class MatchingServiceDB {
    Map<String, String> capsuleMap = new HashMap<>();
    Map<String, String[]> timeStamps = new HashMap<>();
    List<Visit> userLogs = new ArrayList<>();
    Map<LocalDate, byte[]> facilityNyms = new HashMap<>();

    public MatchingServiceDB() {}

    // todo pfffffff
    public void addCapsule(byte[] token, String capsule) {
        capsuleMap.put(bytesToString(token), capsule);
    }
    public void addCapsule(String token, String capsule) {
        capsuleMap.put(token, capsule);
    }
    public boolean hasCapsule(byte [] token) {
        return capsuleMap.containsKey(bytesToString(token));
    }

    public String getCapsule(String token) {
        return capsuleMap.get(token);
    }

    public void flushDB(int today, int INCUBATION_DAYS) {
        ArrayList<String> toRemove = new ArrayList<>();
        for(String date : capsuleMap.keySet()) {
            if(today > stringToDate(date).getDayOfMonth() + INCUBATION_DAYS) {
                toRemove.add(date);
            }
        }
        for(String date : toRemove) {
            capsuleMap.remove(date);
        }

    }

    public void addTimeStamps(String randomToken, String[] timeStamp) {
        if(timeStamps.containsKey(randomToken)) {
            String[] presentStamps = timeStamps.get(randomToken);
            String[] newStamps = new String[presentStamps.length+ timeStamp.length];
            for(int i = 0; i < presentStamps.length; i++) {
                newStamps[i] = presentStamps[i];
            }
            for(int i = 0; i < timeStamp.length; i++) {
                newStamps[presentStamps.length+ i] = timeStamp[i];
            }
            timeStamps.remove(randomToken);
            timeStamps.put(randomToken, newStamps);
        }
        else {
            timeStamps.put(randomToken, timeStamp);
        }
    }

    /******************************** 3. REGISTERING INFECTED USER **********************************/
    // Checks signature and unpacks the data if correct
    public void addSignedLogs(ArrayList<List<byte[]>> signedLogs, PublicKey publicKey) {
        for (List<byte[]> log : signedLogs) {
            ArrayList<byte[]> logPair = new ArrayList<>(log);
            if (checkSignature(logPair, publicKey)) {
                byte[] data = logPair.get(0);
                String dataString = bytesToString(data);
                Visit visit = new Visit(dataString);
                userLogs.add(visit);
                System.out.println("MatchingService: Log signature correct:" + visit);
            }
        }
    }
    // Store all nyms in a map accessible by date
    public void addNym(Map<LocalDate, byte[]> nyms) {
        for (Map.Entry entry : nyms.entrySet()) {
            facilityNyms.put((LocalDate) entry.getKey(), (byte[]) entry.getValue());
        }
        System.out.println("Nyms goed ontvangen door de matching service: "+ facilityNyms);
    }
    public List<String> getCFFromSignedLogs() {
        List<String> CFList = new ArrayList<>();
        for (Visit visit : userLogs) {
            CFList.add(visit.getCF());
        }
        return CFList;
    }
    // Per dag: hash(R_i, nym) =? hash(R_i, nym)
    public void verifyLogs() {
        List<Visit> toRemove = new ArrayList<>();
        for (Visit visit : userLogs) {
            // Find the date of the log
            LocalDateTime dateTime = stringToTimeStamp(visit.getScanTime());
            LocalDate date = dateTime.toLocalDate();
            // Get the according nym
            byte[] nym = facilityNyms.get(date);

            // Check if our own hash is the same is the one provided in the log
            String R_i = visit.getR_i();;
            String nymString = bytesToString(nym);
            String[] data = new String[] {R_i, nymString};
            String dataString = joinStrings(data);
            byte[] ownHash = hash(dataString);
            byte[] givenHash = stringToHash(visit.getH());

            if (Arrays.equals(ownHash, givenHash))
                System.out.println("Hash van de visitor correct geverifieerd: de plaats werd echt bezocht");
            else {
                System.out.println("!!! Data (van de visitor) ingestuurd door de doctor is niet betrouwbaar !!!");
                toRemove.add(visit);
            }
        }

        for (Visit visit : toRemove) userLogs.remove(visit);
    }


    /******************************** 3. REGISTERING INFECTED USER **********************************/


}
