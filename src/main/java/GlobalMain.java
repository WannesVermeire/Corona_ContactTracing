import Doctor.DoctorClient;
import Facility.BarOwnerGUI;
import Facility.Facility;
import Facility.FacilityClient;
import MatchingService.MatchingService;
import MixingProxy.MixingProxy;
import Registrar.Registrar;
import Visitor.Visitor;
import Visitor.VisitorClient;
import Visitor.VisitorGUI;

import java.util.concurrent.TimeUnit;

public class GlobalMain {
    public static void main(String[] args) throws InterruptedException {
        Registrar.main(args);
        MatchingService.main(args);
        MixingProxy.main(args);

        //Todo: Replace FacilityClient with BarOwnerGUI!
        BarOwnerGUI barOwner1 = new BarOwnerGUI(new Facility("Hamann", "Vantegemstraat 3, 9230 Wetteren", "+32 9 333 77 77"));
        //FacilityClient.main(args);

        //Todo: Replace VisitorClient with VisitorGUI!
        VisitorGUI visitor1 = new VisitorGUI(new Visitor("Wannes", "+32 456 30 81 66"));
        //VisitorClient.main(args);


        //TimeUnit.SECONDS.sleep(5); // Anders logfile nog niet volledig geschreven
        //DoctorClient.main(args);

        //MatchingService.getNyms();

    }
}
