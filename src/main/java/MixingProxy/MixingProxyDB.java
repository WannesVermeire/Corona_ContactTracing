package MixingProxy;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.*;

import static Services.Methods.*;

public class MixingProxyDB {
    private static KeyPair keyPair;
    private static Map<String, String> localCacheTokens = new HashMap<String, String>();
    private static Map<String, String[]> localCacheTimeStamps = new HashMap<>();
    public MixingProxyDB() {
        keyPair = getKeyPair();
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public ArrayList<byte[]> signCapsule(String capsule) {
        return getSignature(stringToBytes(capsule), keyPair.getPrivate());
    }

    public void cacheCapsule(byte[] token, String capsule) {
        localCacheTokens.put(bytesToString(token), capsule);
    }
    public boolean containsToken(byte [] token) {
        return localCacheTokens.containsKey(bytesToString(token));
    }

    public boolean isEmptyCapsules() {
        return localCacheTokens.isEmpty();
    }
    public boolean isEmptyTimeStamps() {
        return localCacheTimeStamps.isEmpty();
    }

    public String getRandomTokenCapsule() {
        List<String> keys = new ArrayList<>(localCacheTokens.keySet());
        int randomIndex = new Random().nextInt(keys.size());
        String randomKey = keys.get(randomIndex);
        return randomKey;
    }

    public String getRandomTokenTime() {
        List<String> keys = new ArrayList<>(localCacheTimeStamps.keySet());
        int randomIndex = new Random().nextInt(keys.size());
        String randomKey = keys.get(randomIndex);
        return randomKey;
    }

    public String getCapsule(String token) {
        return localCacheTokens.get(token);
    }

    public void removeToken(String token) {
        localCacheTokens.remove(token);
    }

    public void updateTimeStamp(String tokenWithHash, String timeStamp) {
        if(localCacheTimeStamps.containsKey(tokenWithHash)) {
            String[] current = localCacheTimeStamps.get(tokenWithHash);
            String[] replacement = new String[current.length+1];
            for(int i = 0; i < current.length; i++) {
                replacement[i] = current[i];
            }
            replacement[current.length] = timeStamp;
            localCacheTimeStamps.remove(tokenWithHash);
            localCacheTimeStamps.put(tokenWithHash, replacement);
        }
        else {
            localCacheTimeStamps.put(tokenWithHash, new String[]{timeStamp});
        }
    }

    public String[] getTimeStamp(String randomToken) {
        return localCacheTimeStamps.get(randomToken);
    }

    public void removeTimeStamp(String randomToken) {
        localCacheTokens.remove(randomToken);
    }
}