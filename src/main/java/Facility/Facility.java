package Facility;


import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class Facility implements Serializable {

    private int id; // unique business number
    private String name;
    private String address;
    private String phoneNr;
    private List<SecretKey> keyArray;
    private List<byte[]> nymArray;
    private byte[] random;

    public Facility(int id, String name, String address, String phoneNr) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNr = phoneNr;
        random = new byte[20];
        new Random().nextBytes(random);
    }

    // Gives back unique identifier CF
    public String getCF() { return id+name+address+phoneNr; }

    public int getId() { return id; }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public String getPhoneNr() {
        return phoneNr;
    }
    public List<SecretKey> getKeyArray() {
        return keyArray;
    }
    public List<byte[]> getNymArray() {
        return nymArray;
    }
    public byte[] getRandom() { return random; }

    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setPhoneNr(String phoneNr) {
        this.phoneNr = phoneNr;
    }
    public void setKeyArray(List<SecretKey> keyArray) {
        this.keyArray = keyArray;
    }
    public void setNymArray(List<byte[]> nymArray) {
        this.nymArray = nymArray;
    }

    @Override
    public String toString() {
        String s = "facility{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNr='" + phoneNr + '\'' +
                ", keyArray=" + keyArray +
//                ", nymArray=" + nymArray + "}";
                ", nymArray_size=" + nymArray.size() +
                ", nymArray=[";
        for(byte[] arr : nymArray) {
            for(int i=0; i< arr.length; i++) {
                s+=arr[i];
            }
            s+=',';
        }
        s += "]}";
        return s;
    }
}
