package Interfaces;

import Visitor.Visit;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;

public interface MixingProxyInterface extends Remote {

    ArrayList<byte[]> verifyAndSendConfirmation(Visit visit, PublicKey publicKey) throws Exception;
    PublicKey getPublicKey() throws RemoteException;
    void flushCache() throws RemoteException;
    void updateTimeStamp(String token, String hashValue, String timeStamp) throws RemoteException;
}
