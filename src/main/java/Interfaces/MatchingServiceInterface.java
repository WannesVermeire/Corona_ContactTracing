package Interfaces;

import Doctor.Doctor;
import Registrar.RegistrarDB;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;

public interface MatchingServiceInterface extends Remote {
    boolean containsToken(byte[] token) throws RemoteException;
    void addCapsule(byte[] token, String capsule) throws RemoteException;
    void addCapsule(String token, String capsule) throws RemoteException;
    String getCapsule(String token) throws RemoteException;
    void clearDB(int INCUBATION_DAYS) throws RemoteException;

    void addTimeStamps(String randomToken, String[] timeStamp) throws RemoteException;
    void forwardSickPatientData(String[] signedLogs, PublicKey pubKey) throws RemoteException;
}
