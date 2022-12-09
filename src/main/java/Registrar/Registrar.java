package Registrar;

import javax.crypto.SecretKey;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static Services.Methods.getSecretKey;

/**
 * Enrolls new catering facilities and provides them with a tool to generate QR codes on a daily basis.
 * Enrolls new users and provides them with tokens to be used when visiting catering facilities
 * Reveals contact details of only possibly infected people
 */

public class Registrar {
    // Generate master secret key s
    private static SecretKey s;

    public static void main(String[] args) {
        RegistrarDB registrarDB = new RegistrarDB();
        s = getSecretKey();

        try {
            // create on port 2100
            Registry registry = LocateRegistry.createRegistry(2100);
            // create a new service named RegistrarService
            registry.rebind("RegistrarService", new RegistrarInterfaceImpl(s, registrarDB));

        }
        catch (Exception e) { e.printStackTrace(); }

        System.out.println("Registrar is ready");
    }
}
