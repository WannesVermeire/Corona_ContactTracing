package Interfaces;

import MixingProxy.Capsule;
import Visitor.Visitor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.Calendar;

public interface MixingProxyInterface extends Remote {

    void sendCapsule(Visitor v , Capsule c) throws Exception;
}
