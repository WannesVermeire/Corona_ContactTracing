package MatchingService;

import MixingProxy.Capsule;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MatchingServiceDB {
    Map<String, Capsule> capsluleMap = new HashMap<>();

    public MatchingServiceDB() {}

    public void addCapsule(byte[] token, Capsule capsule) {
        capsluleMap.put(new String(token, StandardCharsets.UTF_8), capsule);
    }
    public boolean hasCapsule(byte [] token) {
        return capsluleMap.containsKey(new String(token, StandardCharsets.UTF_8));
    }
}
