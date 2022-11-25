package Visitor;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class Visitor implements Serializable {

    private String name;
    private String phone;
    private List<byte[]>[] tokens = new List[31];
    private Map<String, String[]> visits;

    PrivateKey privateKey;
    PublicKey publicKey;

    public Visitor(String name, String phone) {
        this.name = name;
        this.phone = phone;
        visits = new HashMap();
    }

    public String getName() { return name; }
    public String getPhone() {
        return phone;
    }
    public void setTokens(List<byte[]>[] tokens) {
        this.tokens = (tokens);
    }
    public void addVisit(String [] log) {
        visits.put(log[3],log);
    }
    public List<byte[]> getTokens(int day) {
        return tokens[day-1];
    }

    public void setKeys(KeyPair pair) {
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();
    }

    public PublicKey getPublicKey () {
        return publicKey;
    }

}
