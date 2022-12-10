package Interfaces;

import MixingProxy.MixingProxyDB;
import Visitor.Visit;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;

public interface MixingProxyInterface extends Remote {
    MixingProxyDB getMixingProxyDB() throws Exception;

    ArrayList<byte[]> verifyAndSendConfirmation(Visit visit, PublicKey publicKey) throws Exception;
    PublicKey getPublicKey() throws RemoteException;
    void flushCache() throws RemoteException;
    void updateTimeStamp(String token, String timeStamp) throws RemoteException;
}
