package Registrar;

import Facility.Facility;

import java.util.ArrayList;
import java.util.List;

public class RegistrarDB {

    private List<Facility> facilities;


    public RegistrarDB() {
        facilities = new ArrayList<>();
    }

    public void addFacility(Facility facility) {
        facilities.add(facility);
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
