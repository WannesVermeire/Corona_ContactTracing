package Interfaces;

import Visitor.MonthSignedTokenList;

import javax.crypto.SecretKey;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public interface EnrollmentInterface extends Remote {

    void registerFacility(String CF, ArrayList<byte[]> signaturePair, PublicKey publicKey) throws RemoteException;

    List<SecretKey> getKeyArray(String id) throws RemoteException;

    List<byte[]> getNymArray(String id) throws RemoteException;

    boolean registerVisitor(String name, String phoneNr) throws RemoteException, IllegalStateException;

    MonthSignedTokenList getSignedTokens(String phoneNr) throws RemoteException;

    int getINCUBATION_DAYS() throws RemoteException;

    PublicKey getPublicKey() throws RemoteException;

//    void visitFacility_scan(Visitor v, String qr_scanned);


    public List<byte[]> getAllNym() throws RemoteException;


}
