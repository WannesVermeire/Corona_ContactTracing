import Doctor.Doctor;
import Doctor.DoctorGUI;
import Facility.BarOwnerGUI;
import Facility.Facility;
import MatchingService.MatchingService;
import MixingProxy.MixingProxy;
import Registrar.Registrar;
import Visitor.Visitor;
import Visitor.VisitorGUI;

import java.util.concurrent.TimeUnit;

public class GlobalMain {
    public static void main(String[] args) throws InterruptedException {
        Registrar.main(args);
        MatchingService.main(args);
        MixingProxy.main(args);


        BarOwnerGUI barOwner1 = new BarOwnerGUI(new Facility("Hamann", "Vantegemstraat 3, 9230 Wetteren", "+32 9 333 77 77"));
        VisitorGUI visitor1 = new VisitorGUI(new Visitor("Wannes", "+32 456 30 81 66"));
        DoctorGUI doctor1 = new DoctorGUI(new Doctor("Toon Eeraerts"));

//        MatchingService.getNyms();

    }
}
