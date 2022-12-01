package MatchingService;

import Interfaces.MatchingServiceInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MatchingServiceInterfaceImplementation extends UnicastRemoteObject implements MatchingServiceInterface {
    private MatchingServiceDB matchingServiceDB;

    public MatchingServiceInterfaceImplementation(MatchingServiceDB matchingServiceDB) throws RemoteException{
        this.matchingServiceDB = matchingServiceDB;
    }
    @Override
    public boolean containsToken(byte[] token)  {
        return matchingServiceDB.hasCapsule(token);
    }
    @Override
    public void addCapsule(byte[] token, String capsule) throws RemoteException {
        matchingServiceDB.addCapsule(token, capsule);
    }

}
