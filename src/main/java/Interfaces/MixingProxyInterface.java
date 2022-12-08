package Interfaces;

import Visitor.Visitor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;

public interface MixingProxyInterface extends Remote {

    ArrayList<byte[]> verifyAndSendConfirmation(Visitor visitor, PublicKey publicKey, String scanTime, ArrayList<byte[]> tokenPair, byte[] hashValue) throws Exception;
    PublicKey getPublicKey() throws RemoteException;
    void flushCache() throws RemoteException;
    void updateTimeStamp(String token, String hashValue, String timeStamp) throws RemoteException;
}
