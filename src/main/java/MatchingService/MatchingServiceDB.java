package MatchingService;

import Visitor.Visit;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Services.Methods.*;

public class MatchingServiceDB {
    Map<String, String> capsuleMap = new HashMap<>();
    Map<String, String[]> timeStamps = new HashMap<>();
    List<Visit> userLogs = new ArrayList<>();

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
                System.out.println("Log signature correct:" +visit);
            }
        }


    }

    /******************************** 3. REGISTERING INFECTED USER **********************************/


}
