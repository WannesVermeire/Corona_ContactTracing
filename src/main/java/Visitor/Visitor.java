package Visitor;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class Visitor implements Serializable {

    private String name;
    private String phone;
    private List<byte[]> tokens;
    private Map<Integer, String[]> visits;

    public Visitor(String name, String phone) {
        this.name = name;
        this.phone = phone;
        visits = new HashMap();
    }

    public String getName() { return name; }
    public String getPhone() {
        return phone;
    }
    public void setTokens(List<byte[]> tokens) {
        this.tokens = tokens;
    }
    public void addVisit(String [] log, int day) {
        visits.put(day, log);
    }
    public byte[] getToken(int day) {
        return tokens.get(day);
    }

}
