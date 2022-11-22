package Registrar;

import Facility.Facility;
import Visitor.Visitor;

import java.util.ArrayList;
import java.util.List;
//Saves every facility & visitor registered
public class RegistrarDB {

    private List<Facility> facilities;
    private List<Visitor> visitors;


    public RegistrarDB() {
        facilities = new ArrayList<>();
        visitors = new ArrayList<>();
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

    public Facility findFacilityById(int id) {
        Facility res = null;
        for (Facility f : facilities)
            if (f.getId()==id) {
                res = f;
                break;
            }

        return res;
    }
}
