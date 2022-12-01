package Interfaces;

import Visitor.Visitor;

import java.rmi.Remote;
import java.security.PublicKey;
import java.util.ArrayList;

public interface MixingProxyInterface extends Remote {

    void sendCapsule(Visitor visitor, PublicKey publicKey, String scanTime, ArrayList<byte[]> tokenPair, byte[] hashValue) throws Exception;
}
