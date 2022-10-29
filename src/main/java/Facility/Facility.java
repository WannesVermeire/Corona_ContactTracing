package Facility;


public class Facility {

    private int businessNr;
    private String name;
    private String address;
    private String phoneNr;

    public Facility(int businessNr, String name, String address, String phoneNr) {
        this.businessNr = businessNr;
        this.name = name;
        this.address = address;
        this.phoneNr = phoneNr;
    }

    public int getBusinessNr() {return businessNr; }
    public String getName() { return name; }
    public String getAddress() {
        return address;
    }
    public String getPhoneNr() {
        return phoneNr;
    }

    public String getCF() {
        return businessNr+name+address+phoneNr;
    }


}
