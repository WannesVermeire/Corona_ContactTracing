package MixingProxy;

import Interfaces.MixingProxyInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MixingProxyInterfaceImplementation extends UnicastRemoteObject implements MixingProxyInterface {
    public MixingProxyInterfaceImplementation() throws RemoteException{}

    public void sendCapsule(){

    };
}
