package Interfaces;

import Facility.Facility;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface EnrollmentInterface extends Remote {

    void registerFacility(String CF) throws RemoteException;

    /*

    void sendMessage(String message) throws RemoteException;
    ArrayList<String> receiveMessage() throws RemoteException;

     */



}
