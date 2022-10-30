package Visitor;

import java.io.Serializable;

public class Visitor implements Serializable {

    private String name;
    private String phone;

    public Visitor(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() { return name; }
    public String getPhone() {
        return phone;
    }
}
