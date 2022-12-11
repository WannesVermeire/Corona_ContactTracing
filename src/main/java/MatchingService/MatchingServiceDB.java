package MatchingService;

import MixingProxy.Entry;
import Visitor.Visit;
import org.apache.commons.lang3.ArrayUtils;

import java.security.PublicKey;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static Services.Methods.*;

public class MatchingServiceDB {
    // capsuleMap en timeStamps zijn tussenstap voor omzetting naar Entries
    private Map<String, Visit> capsuleMap = new HashMap<>(); // key = token, data: is Visit
    private Map<String, String[]> timeStamps = new HashMap<>(); // key = token, data: array van timestamps

    private List<Visit> userLogs = new ArrayList<>();
    private Map<LocalDate, byte[]> facilityNyms = new HashMap<>();
    private List<Entry> allEntries = new ArrayList<>();


    public MatchingServiceDB() {}

    public Map<String, Visit> getCapsuleMap() {
        return capsuleMap;
    }

    public Map<String, String[]> getTimeStamps() {
        return timeStamps;
    }

    public List<Visit> getUserLogs() {
        return userLogs;
    }

    public Map<LocalDate, byte[]> getFacilityNyms() {
        return facilityNyms;
    }

    public List<Entry> getAllEntries() {
        return allEntries;
    }

    public void addCapsule(byte[] token, Visit capsule) {
        capsuleMap.put(bytesToString(token), capsule);
    }
    public void addCapsule(String token, Visit capsule) {
        System.out.println("Capsule goed ontvangen in matching service: "+capsule);
        capsuleMap.put(token, capsule);
    }
    public boolean hasCapsule(byte [] token) {
        return capsuleMap.containsKey(bytesToString(token));
    }
    public Visit getCapsule(String token) {
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

    public void addTimeStamps(String token, String[] timeStampList) {
        System.out.println("Timestamps goed ontvangen in matching service: "+Arrays.toString(timeStampList));
        if(timeStamps.containsKey(token)) {
            String[] oldTimeStamps = timeStamps.get(token);
            String[] newTimeStamps = ArrayUtils.addAll(oldTimeStamps, timeStampList);
            timeStamps.remove(token);
            timeStamps.put(token, newTimeStamps);
        }
        else {
            timeStamps.put(token, timeStampList);
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
            String R_i = visit.getR_i();
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
    // An entry combines all the info from
    public void generateEntries() {
        for (var var : capsuleMap.entrySet()) {
            String token = var.getKey();
            Visit visit = var.getValue();

            // Get lower en upperbound for timeInterval
            String[] timeStampStrings = timeStamps.get(token);
            LocalDateTime[] timeStampList = new LocalDateTime[timeStampStrings.length];
            for (int i = 0; i < timeStampStrings.length; i++) {
                timeStampList[i] = stringToTimeStamp(timeStampStrings[i]);
            }
            LocalDateTime min = LocalDateTime.MAX;
            LocalDateTime max = LocalDateTime.MIN;
            for (LocalDateTime time : timeStampList) {
                if (time.isBefore(min)) min = time;
                if (time.isAfter(max)) max = time;
            }

            // Save Entry
            byte[] tokenBytes = stringToHash(token);
            byte[] hash = stringToHash(visit.getH());
            Entry entry = new Entry(tokenBytes, hash, min, max);
            allEntries.add(entry);
        }
        System.out.println("Alle entries generated in the Matching Service: "+allEntries);
        capsuleMap.clear();
        timeStamps.clear();
    }
    // Mark all infected entries based on hashes in user logs
    public void markInfectedCapsules() {
        for (Visit visit : userLogs) {
            byte[] hash = stringToHash(visit.getH());
            for (Entry entry : allEntries) {
                if (Arrays.equals(hash, entry.getHash())) {
                    entry.setCritical(true);
                }
            }
        }
        System.out.println("Alle entries na aanduiden van geïnfecteerde facilities: "+allEntries);
    }
    // Mark the notifier of this infection as already informed
    public void markInfectedTokens() {
        for (Visit visit : userLogs) {
            byte[] token = visit.getTokenPair().get(0);
            for (Entry entry : allEntries) {
                if (Arrays.equals(token, entry.getToken())) {
                    entry.setInformed(true);
                }
            }
        }
        System.out.println("Alle entries na aanduiden van geïnformeerde visitors: "+allEntries);
    }
    /******************************** 3. REGISTERING INFECTED USER **********************************/

    /**************************** 4. INFORMING POSSIBLY INFECTED USERS ******************************/
    public List<Entry> getInfectedEntries() {
        List<Entry> infectedEntries = new ArrayList<>();
        for (Entry entry : allEntries) {
            if(entry.isCritical()) infectedEntries.add(entry);
        }
        return infectedEntries;
    }

    public void notifyReceived(String token) {
        int counter = 0;
        for (Entry entry : allEntries) {
            if (Arrays.equals(entry.getToken(), stringToHash(token))) {
                entry.setInformed(true);
                counter++;
            }
        }
        System.out.println(counter + " entrties/tokens marked as informed");
        System.out.println("Entries na informed marking: "+allEntries);
    }


    /**************************** 4. INFORMING POSSIBLY INFECTED USERS ******************************/
    public List<Entry> getNonInformedEntries() {
        List<Entry> nonInformed = new ArrayList<>();
        for(Entry entry : allEntries) {
            if(!entry.isInformed() && entry.isCritical()) {
                nonInformed.add(entry);
                entry.setInformed(true);
            }
        }
        return nonInformed;
    }

}
