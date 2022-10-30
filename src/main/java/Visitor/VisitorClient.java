package Visitor;

import Interfaces.EnrollmentInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class VisitorClient {
    public static void main(String[] args) {
        Visitor visitor = new Visitor("Wannes", "+32 456 30 81 62");

        try {
            // fire to localhost port 2100
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);

            // search for RegistrarService
            EnrollmentInterface impl = (EnrollmentInterface) myRegistry.lookup("RegistrarService");

            // call server methods
            impl.registerVisitor(visitor);

        } catch (Exception e) { e.printStackTrace(); }

    }
}
