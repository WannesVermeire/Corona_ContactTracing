package Interfaces;

import MixingProxy.Entry;
import Visitor.Visit;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.time.LocalDateTime;
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

    /**************************** 4. INFORMING POSSIBLY INFECTED USERS ******************************/
    List<Entry> getInfectedEntries() throws RemoteException;

    void notifyReceived(String hash) throws RemoteException;

    /**************************** 4. INFORMING POSSIBLY INFECTED USERS ******************************/

//    void transferNonInformed() throws RemoteException;
}
