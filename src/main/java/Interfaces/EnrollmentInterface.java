package Interfaces;

import Facility.Facility;
import Visitor.Visitor;

import javax.crypto.SecretKey;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.List;

public interface EnrollmentInterface extends Remote {

    void registerFacility(String CF, PublicKey publicKey, byte[] signature) throws RemoteException;

    List<SecretKey> getKeyArray(String id) throws RemoteException;

    List<byte[]> getNymArray(String id) throws RemoteException;

    Visitor registerVisitor(Visitor visitor) throws RemoteException, IllegalStateException;

    int getINCUBATION_DAYS() throws RemoteException;



//    void visitFacility_scan(Visitor v, String qr_scanned);
}
