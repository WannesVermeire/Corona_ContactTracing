package MixingProxy;

import Visitor.Visit;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

import static Services.Methods.*;

public class MixingProxyDB {
    private static KeyPair keyPair;
    private Map<String, Visit> capsuleMap = new HashMap<>(); // key = token, data: is Visit
    private Map<String, String[]> timeStamps = new HashMap<>(); // key = token, data: array van timestamps

    public MixingProxyDB() {
        keyPair = getKeyPair();
    }
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }
    public PrivateKey getSecretKey() {
        return keyPair.getPrivate();
    }

    public Map<String, Visit> getCapsuleMap() {
        return capsuleMap;
    }

    public Map<String, String[]> getTimeStamps() {
        return timeStamps;
    }

    public ArrayList<byte[]> signCapsule(String capsule) {
        return getSignature(stringToBytes(capsule), keyPair.getPrivate());
    }
    public void addCapsule(String token, Visit capsule) {
        capsuleMap.put(token, capsule);
    }
    public void addCapsule(Visit visit) {
        String token = hashToString(visit.getTokenPair().get(0));
        capsuleMap.put(token, visit);
    }
    public boolean containsCapsule(byte [] token) {
        return capsuleMap.containsKey(bytesToString(token));
    }
    public boolean isEmptyCapsules() {
        return capsuleMap.isEmpty();
    }
    public boolean isEmptyTimeStamps() {
        return timeStamps.isEmpty();
    }
    public String getRandomTokenCapsule() {
        List<String> keys = new ArrayList<>(capsuleMap.keySet());
        int randomIndex = new Random().nextInt(keys.size());
        String randomKey = keys.get(randomIndex);
        return randomKey;
    }
    public String getRandomTokenTime() {
        List<String> keys = new ArrayList<>(timeStamps.keySet());
        int randomIndex = new Random().nextInt(keys.size());
        String randomKey = keys.get(randomIndex);
        return randomKey;
    }
    public Visit getCapsule(String token) {
        return capsuleMap.get(token);
    }
    public void removeToken(String token) {
        capsuleMap.remove(token);
    }
    public String[] getTimeStamp(String randomToken) {
        return timeStamps.get(randomToken);
    }
    public void removeTimeStamp(String randomToken) {
        timeStamps.remove(randomToken);
    }



    public void updateTimeStamp(String token, String timeStamp) {
        if(timeStamps.containsKey(token)) {
            String[] current = timeStamps.get(token);
            String[] replacement = new String[current.length+1];
            for(int i = 0; i < current.length; i++) {
                replacement[i] = current[i];
            }
            replacement[current.length] = timeStamp;
            timeStamps.remove(token);
            timeStamps.put(token, replacement);
        }
        else {
            timeStamps.put(token, new String[]{timeStamp});
        }
    }




}
