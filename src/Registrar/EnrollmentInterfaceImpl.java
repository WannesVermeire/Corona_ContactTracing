package Registrar;

import Interfaces.EnrollmentInterface;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class EnrollmentInterfaceImpl extends UnicastRemoteObject implements EnrollmentInterface {

    ArrayList<String> messages = new ArrayList<>(6);

    public EnrollmentInterfaceImpl() throws RemoteException {}


    /** Phase 1.1: Enrollment owner **/
    // Generate master secret key s



//    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//    SecretKey sKey = keyGen.generateKey();




}
