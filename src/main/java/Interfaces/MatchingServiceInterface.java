package Interfaces;

import Registrar.RegistrarDB;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatchingServiceInterface extends Remote {
    boolean containsToken(byte[] token) throws RemoteException;
    void addCapsule(byte[] token, String joinStrings);
}
