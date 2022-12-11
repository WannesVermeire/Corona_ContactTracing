package Interfaces;
import Visitor.Visit;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;

public interface MixingProxyInterface extends Remote {

    ArrayList<byte[]> verifyAndSendConfirmation(Visit visit, PublicKey publicKey) throws Exception, RemoteException;

    PublicKey getPublicKey() throws RemoteException;

    void flushCache() throws RemoteException;

    void updateTimeStamp(String token, String timeStamp) throws RemoteException;
}
