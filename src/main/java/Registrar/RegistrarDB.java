package Registrar;

import Facility.Facility;
import Visitor.Visitor;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static Services.Methods.getKeyPair;

//Saves every facility & visitor registered
public class RegistrarDB {

    private List<Facility> facilities;
    private List<Visitor> visitors;
    private static KeyPair keyPair;


    public RegistrarDB() {
        facilities = new ArrayList<>();
        visitors = new ArrayList<>();
        keyPair = getKeyPair();
    }

    public void addFacility(Facility facility) {
        facilities.add(facility);
    }
    public void addVisitor(Visitor visitor) {
        for (Visitor v : visitors) {
            if (v.getPhone().equals(visitor.getPhone()))
                throw new IllegalStateException("Visitor with this phone nr already exists");
        }
        visitors.add(visitor);
    }

    public Facility findFacilityById(String id) {
        Facility res = null;
        for (Facility f : facilities)
            if (f.getId().equals(id)) {
                res = f;
                break;
            }

        return res;
    }

    public PublicKey getPublicKey () {
        return keyPair.getPublic();
    }
    public PrivateKey getPrivate () {
        return keyPair.getPrivate();
    }

}
