package MatchingService;

import Interfaces.MatchingServiceInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MatchingServiceInterfaceImplementation extends UnicastRemoteObject implements MatchingServiceInterface {
    public MatchingServiceInterfaceImplementation(MatchingServiceDB matchingServiceDB) throws RemoteException{
        this.matchingServiceDB = matchingServiceDB;
    }
    private MatchingServiceDB matchingServiceDB;
//    @Override
//    public boolean isTokenUsed(byte[] token)  {
//        return matchingServiceDB.hasCapsule(token);
//    }
}
