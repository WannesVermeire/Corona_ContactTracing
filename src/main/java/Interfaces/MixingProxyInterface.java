package Interfaces;

import Visitor.Visitor;

import java.rmi.Remote;
import java.security.PublicKey;

public interface MixingProxyInterface extends Remote {

    void sendCapsule(PublicKey publicKey , String scanTime, byte[] signedToken, byte[] unsignedToken, byte[] hash) throws Exception;
}
