package MatchingService;

import java.util.HashMap;
import java.util.Map;

import static Services.Methods.bytesToString;

public class MatchingServiceDB {
    Map<String, String> capsuleMap = new HashMap<>();

    public MatchingServiceDB() {}

    public void addCapsule(byte[] token, String capsule) {
        capsuleMap.put(bytesToString(token), capsule);
    }
    public boolean hasCapsule(byte [] token) {
        return capsuleMap.containsKey(bytesToString(token));
    }


}
