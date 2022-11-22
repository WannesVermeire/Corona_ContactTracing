package Interfaces;

import Facility.Facility;
import Visitor.Visitor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface EnrollmentInterface extends Remote {

    void registerFacility(Facility facility) throws RemoteException;

    List<byte[]> getNymArray(int id) throws RemoteException;

    Visitor registerVisitor(Visitor visitor) throws RemoteException, IllegalStateException;

    int getINCUBATION_DAYS() throws RemoteException;

//    void visitFacility_scan(Visitor v, String qr_scanned);
}
