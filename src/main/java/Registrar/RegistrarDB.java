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

    private Map<String, Facility> facilities;
    private List<Visitor> visitors;
    private static KeyPair keyPair;


    public RegistrarDB() {
        facilities = new HashMap<String, Facility>();
        visitors = new ArrayList<>();
        keyPair = getKeyPair();
    }

    public void addFacility(Facility facility) {
        facilities.put(facility.getCF(), facility);
    }
    public void addVisitor(Visitor visitor) {
        for (Visitor v : visitors) {
            if (v.getPhone().equals(visitor.getPhone()))
                throw new IllegalStateException("Visitor with this phone nr already exists");
        }
        visitors.add(visitor);
    }

    public Facility findFacilityById(String id) {
        for (Facility f : facilities.values())
            if (f.getId().equals(id)) {
                return f;
            }
        return null;
    }

    public PublicKey getPublicKey () {
        return keyPair.getPublic();
    }
    public PrivateKey getPrivate () {
        return keyPair.getPrivate();
    }

}
