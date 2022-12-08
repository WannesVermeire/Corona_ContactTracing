import Doctor.DoctorClient;
import Facility.FacilityClient;
import MatchingService.MatchingService;
import MixingProxy.MixingProxy;
import Registrar.Registrar;
import Visitor.VisitorClient;
import java.util.concurrent.TimeUnit;

public class GlobalMain {
    public static void main(String[] args) throws InterruptedException {
        Registrar.main(args);
        MatchingService.main(args);
        MixingProxy.main(args);

        FacilityClient.main(args);
        VisitorClient.main(args);
        TimeUnit.SECONDS.sleep(5); // Anders logfile nog niet volledig geschreven
        DoctorClient.main(args);

    }
}
