package Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MixingProxyInterface extends Remote {
    void sendCapsule() throws RemoteException;
}
