package Interfaces;

import MixingProxy.Entry;
import Visitor.Visit;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public interface MixingProxyInterface extends Remote {

    ArrayList<byte[]> verifyAndSendConfirmation(Visit visit, PublicKey publicKey) throws Exception;
    PublicKey getPublicKey() throws RemoteException;
    void flushCache() throws RemoteException;
    void updateTimeStamp(String token, String timeStamp) throws RemoteException;

    void notifyNonInformed(List<Entry> nonInformed) throws RemoteException;
}
