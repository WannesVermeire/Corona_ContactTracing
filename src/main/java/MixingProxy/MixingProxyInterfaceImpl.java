package MixingProxy;

import Interfaces.MatchingServiceInterface;
import Interfaces.MixingProxyInterface;
import Visitor.Visitor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.ArrayList;

import static Services.Methods.checkSignature;

public class MixingProxyInterfaceImpl extends UnicastRemoteObject implements MixingProxyInterface {
    MatchingServiceInterface impl;
    public MixingProxyInterfaceImpl() throws RemoteException, NotBoundException {
        // matchingRegistry is a reference (stub) for the registry that is running on port 2300 a.k.a. the MatchingService
        Registry matchingRegistry = LocateRegistry.getRegistry("localhost", 2300);
        // Obtain the stub for the remote object with name "MatchingService" a.k.a. the MatchingServiceInterfaceImplementation
        this.impl = (MatchingServiceInterface) matchingRegistry.lookup("MatchingService");
    }

    /** 2.1 Visit facility **/
    @Override
    public void sendCapsule(Visitor visitor, PublicKey publicKey, String scanTime, ArrayList<byte[]> tokenPair, byte[] hashValue) throws Exception {
        // 3 checks: signature, day, not yet used
        if(checkSignature(tokenPair, publicKey)) {
            if(true) {
//                if(!impl.isTokenUsed(c.getToken())) {
                if((true)) {
                    System.out.println("Great Succes");
                }
                else throw new Exception("Token is already used");
            }
            else throw new Exception("Date of token is not correct");
        }
        else {
            throw new SignatureException("Invalid signature");
        }
    }
}
