package MixingProxy;

import Interfaces.MixingProxyInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MixingProxyInterfaceImplementation extends UnicastRemoteObject implements MixingProxyInterface {
    public MixingProxyInterfaceImplementation() throws RemoteException{}

    /** 2.1 Visit facility **/
    @Override
    public void sendCapsule(byte[] token) {
        // TODO : tot hier geraakt....
    }
}
