package Interfaces;

import Visitor.Visit;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public interface MatchingServiceInterface extends Remote {
    boolean containsToken(byte[] token) throws RemoteException;
    void addCapsule(byte[] token, Visit capsule) throws RemoteException;
    void addCapsule(String token, Visit capsule) throws RemoteException;
    Visit getCapsule(String token) throws RemoteException;
    void clearDB(int INCUBATION_DAYS) throws RemoteException;

    void addTimeStamps(String randomToken, String[] timeStamp) throws RemoteException;

    /******************************** 3. REGISTERING INFECTED USER **********************************/
    void receiveSignedLogs(ArrayList<List<byte[]>> signedLogs, PublicKey publicKey) throws RemoteException, NotBoundException;
    /******************************** 3. REGISTERING INFECTED USER **********************************/

}
