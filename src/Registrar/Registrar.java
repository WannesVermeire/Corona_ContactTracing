package Registrar;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Enrolls new catering facilities and provides them with a tool to generate QR codes on a daily basis.
 * Enrolls new users and provides them with tokens to be used when visiting catering facilities
 * Reveals contact details of only possibly infected people
 */

public class Registrar {
    public void execute() {
        try {
            // create on port 2100
            Registry registry = LocateRegistry.createRegistry(2100);
            // create a new service named RegistrarService
            registry.rebind("RegistrarService", new EnrollmentInterfaceImpl());


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Registrar is ready");
    }


    public static void main(String[] args) {
        Registrar registrar = new Registrar();
        registrar.execute();
    }
}
