package MatchingService;

import Interfaces.MatchingServiceInterface;
import Interfaces.MixingProxyInterface;
import MixingProxy.Entry;
import Visitor.Visit;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static Services.Methods.*;

public class MatchingServiceInterfaceImpl extends UnicastRemoteObject implements MatchingServiceInterface {
    private MatchingServiceDB matchingServiceDB;

    public MatchingServiceInterfaceImpl(MatchingServiceDB matchingServiceDB) throws RemoteException, NotBoundException {
        this.matchingServiceDB = matchingServiceDB;
    }

    @Override
    public boolean containsToken(byte[] token)  {
        return matchingServiceDB.hasCapsule(token);
    }
    @Override
    public void addCapsule(byte[] token, Visit capsule) throws RemoteException {
        matchingServiceDB.addCapsule(token, capsule);
    }
    @Override
    public void addCapsule(String token, Visit capsule) throws RemoteException {
        matchingServiceDB.addCapsule(token, capsule);
    }
    public Visit getCapsule(String token) throws RemoteException {
        return matchingServiceDB.getCapsule(token);
    }

    @Override
    public void clearDB(int INCUBATION_DAYS) throws RemoteException {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        matchingServiceDB.flushDB(today, INCUBATION_DAYS);
    }

    @Override
    public void addTimeStamps(String token, String[] timeStamp) {
        matchingServiceDB.addTimeStamps(token, timeStamp);
    }
    /******************************** 3. REGISTERING INFECTED USER **********************************/
    public void receiveSignedLogs(ArrayList<List<byte[]>> signedLogs, PublicKey publicKey) throws RemoteException, NotBoundException {
        connectToMixingProxy().flushCache();
        System.out.println("Cache is flushed");
        matchingServiceDB.addSignedLogs(signedLogs, publicKey);
    }
    /******************************** 3. REGISTERING INFECTED USER **********************************/


    /**************************** 4. INFORMING POSSIBLY INFECTED USERS ******************************/
    public List<Entry> getInfectedEntries() {
        return matchingServiceDB.getInfectedEntries();
    }

    @Override
    public void notifyReceived(String hash) throws RemoteException {
        matchingServiceDB.notifyReceived(hash);
    }

    /**************************** 4. INFORMING POSSIBLY INFECTED USERS ******************************/

    @Override
    public void transferNonInformed() throws RemoteException, NotBoundException {
        List<Entry> nonInformed = matchingServiceDB.getNonInformedEntries();
        if(nonInformed.isEmpty()) {
            System.out.println("Everyone was informed");
        }
        else {
            connectToMixingProxy().notifyNonInformed(nonInformed);
            System.out.println("Transfered & notified all non-informed tokens to the Mixing Proxy");
        }
    }

}
