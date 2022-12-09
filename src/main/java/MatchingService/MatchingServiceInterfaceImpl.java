package MatchingService;

import Interfaces.MatchingServiceInterface;
import Interfaces.MixingProxyInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static Services.Methods.*;

public class MatchingServiceInterfaceImpl extends UnicastRemoteObject implements MatchingServiceInterface {
    private MatchingServiceDB matchingServiceDB;
    private MixingProxyInterface mixingProxy;

    public MatchingServiceInterfaceImpl(MatchingServiceDB matchingServiceDB) throws RemoteException, NotBoundException {
        this.matchingServiceDB = matchingServiceDB;
    }
    public void connectToMixingProxy() throws NotBoundException, RemoteException {
        Registry mixingProxyRegistry = LocateRegistry.getRegistry("localhost", 2200);
        this.mixingProxy = (MixingProxyInterface) mixingProxyRegistry.lookup("MixingProxyService");
    }
    @Override
    public boolean containsToken(byte[] token)  {
        return matchingServiceDB.hasCapsule(token);
    }
    @Override
    public void addCapsule(byte[] token, String capsule) throws RemoteException {
        matchingServiceDB.addCapsule(token, capsule);
    }    @Override
    public void addCapsule(String token, String capsule) throws RemoteException {
        matchingServiceDB.addCapsule(token, capsule);
    }
    public String getCapsule(String token) throws RemoteException {
        return matchingServiceDB.getCapsule(token);
    }

    @Override
    public void clearDB(int INCUBATION_DAYS) throws RemoteException {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        matchingServiceDB.flushDB(today, INCUBATION_DAYS);
    }

    @Override
    public void addTimeStamps(String randomToken, String[] timeStamp) {
        matchingServiceDB.addTimeStamps(randomToken, timeStamp);
    }
    /******************************** 3. REGISTERING INFECTED USER **********************************/
    public void receiveSignedLogs(ArrayList<List<byte[]>> signedLogs, PublicKey publicKey) throws RemoteException, NotBoundException {
        connectToMixingProxy();
        mixingProxy.flushCache();
        System.out.println("Cache is flushed");
        matchingServiceDB.addSignedLogs(signedLogs, publicKey);
    }
    /******************************** 3. REGISTERING INFECTED USER **********************************/


}
