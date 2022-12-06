import Facility.FacilityClient;
import MatchingService.MatchingService;
import MixingProxy.MixingProxy;
import Registrar.Registrar;
import Visitor.VisitorClient;
import com.google.zxing.NotFoundException;

import java.io.IOException;

public class GlobalMain {
    public static void main(String[] args) throws NotFoundException, IOException {
        Registrar.main(args);
        MatchingService.main(args);
        MixingProxy.main(args);

        FacilityClient.main(args);
        VisitorClient.main(args);

    }
}
