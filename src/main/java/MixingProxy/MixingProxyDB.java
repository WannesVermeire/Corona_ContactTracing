package MixingProxy;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static Services.Methods.*;

public class MixingProxyDB {
    private static KeyPair keyPair;
    private static Map<String, String> localCacheTokens = new HashMap<String, String>();
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

}
