package Registrar;

import Facility.Facility;
import Visitor.Visitor;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Services.Methods.getKeyPair;

//Saves every facility & visitor registered
public class RegistrarDB {

    private Map<String, Facility> facilities; // key = CF
    private Map<String, Visitor> visitors; // key = phoneNr
    private static KeyPair keyPair;


    public RegistrarDB() {
        facilities = new HashMap<>();
        visitors = new HashMap<>();
        keyPair = getKeyPair();
    }

    public Map<String, Facility> getFacilities() {
        return facilities;
    }

    public Map<String, Visitor> getVisitors() {
        return visitors;
    }

    /************************************* 1.1 FACILITY ENROLLMENT *************************************/
    public Facility findFacilityById(String id) {
        for (Facility f : facilities.values())
            if (f.getId().equals(id)) return f;
        return null;
    }
    public void addFacility(Facility facility) {
        facilities.put(facility.getCF(), facility);
    }
    /************************************* 1.1 FACILITY ENROLLMENT *************************************/


    /************************************* 1.2 USER ENROLLMENT *************************************/
    public Visitor findVisitorByPhoneNr(String phoneNr) {return visitors.get(phoneNr);}
    public boolean visitorExists(String phoneNr) {
        return visitors.containsKey(phoneNr);
    }
    public void addVisitor(Visitor visitor) {
        visitors.put(visitor.getPhoneNr(), visitor);
    }
    /************************************* 1.2 USER ENROLLMENT *************************************/

    public PublicKey getPublicKey () {
        return keyPair.getPublic();
    }
    public PrivateKey getPrivate () {
        return keyPair.getPrivate();
    }

}
