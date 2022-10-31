package Visitor;

import java.io.Serializable;
import java.util.List;

public class Visitor implements Serializable {

    private String name;
    private String phone;
    private List<byte[]> tokens;

    public Visitor(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() { return name; }
    public String getPhone() {
        return phone;
    }
    public void setTokens(List<byte[]> tokens) {
        this.tokens = tokens;
    }
}
