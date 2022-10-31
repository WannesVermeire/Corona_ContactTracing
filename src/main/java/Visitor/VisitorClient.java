package Visitor;

import Interfaces.EnrollmentInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class VisitorClient {
    public static void main(String[] args) {
        Visitor visitor = new Visitor("Wannes", "+32 456 30 81 66");

        try {
            // fire to localhost port 2100
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);

            // search for RegistrarService
            EnrollmentInterface impl = (EnrollmentInterface) myRegistry.lookup("RegistrarService");

            // call server methods
            impl.registerVisitor(visitor);

            System.out.println("Succesfully registered to the system");
        } catch (Exception e) { e.printStackTrace(); }

    }
}
