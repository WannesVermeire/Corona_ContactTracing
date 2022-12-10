package MixingProxy;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Shuffles incoming messages
 * Flushes them at regular times to the matching service
 */

public class MixingProxy {
    public static void main(String[] args) {
        try {
            //Launch mixing proxy on port 2200
            Registry mixingRegistry = LocateRegistry.createRegistry(2200);

            // create a new service named MixingProxyService
            mixingRegistry.rebind("MixingProxyService", new MixingProxyGUI());

        }
        catch (Exception e) { e.printStackTrace(); }
        System.out.println("Mixing Proxy is ready");
    }
}
