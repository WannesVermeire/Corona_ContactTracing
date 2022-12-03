package Interfaces;

import Registrar.RegistrarDB;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatchingServiceInterface extends Remote {
    boolean containsToken(byte[] token) throws RemoteException;
    void addCapsule(byte[] token, String capsule) throws RemoteException;
    void addCapsule(String token, String capsule) throws RemoteException;
    String getCapsule(String token) throws RemoteException;
    void clearDB(int INCUBATION_DAYS) throws RemoteException;

    void addTimeStamps(String randomToken, String[] timeStamp) throws RemoteException;
}
