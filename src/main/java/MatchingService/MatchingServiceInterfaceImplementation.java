package MatchingService;

import Interfaces.MatchingServiceInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;

import static Services.Methods.stringToDate;

public class MatchingServiceInterfaceImplementation extends UnicastRemoteObject implements MatchingServiceInterface {
    private MatchingServiceDB matchingServiceDB;

    public MatchingServiceInterfaceImplementation(MatchingServiceDB matchingServiceDB) throws RemoteException {
        this.matchingServiceDB = matchingServiceDB;
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

    @Override
    public void forwardSickPatientData(ArrayList signedLogs){

    }

}
