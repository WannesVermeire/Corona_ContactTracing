package MixingProxy;

import Interfaces.MatchingServiceInterface;
import Interfaces.MixingProxyInterface;
import MatchingService.MatchingService;
import MatchingService.MatchingServiceInterfaceImplementation;
import Visitor.Visitor;

import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.Calendar;

public class MixingProxyInterfaceImplementation extends UnicastRemoteObject implements MixingProxyInterface {
    MatchingServiceInterface impl;
    public MixingProxyInterfaceImplementation() throws RemoteException, NotBoundException {
        // matchingRegistry is a reference (stub) for the registry that is running on port 2300 a.k.a. the MatchingService
        Registry matchingRegistry = LocateRegistry.getRegistry("localhost", 2300);
        // Obtain the stub for the remote object with name "MatchingService" a.k.a. the MatchingServiceInterfaceImplementation
        this.impl = (MatchingServiceInterface) matchingRegistry.lookup("MatchingService");
    }

    /** 2.1 Visit facility **/
    @Override
    public void sendCapsule(Visitor v , int timeInterval, byte[] token, String H) throws Exception {
        Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
        dsa.initVerify(v.getPublicKey());
        // 3 checks: signature, day, not yet used
        if(dsa.verify(token)) {
            if(true) {
                if(!impl.isTokenUsed(token)) {
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
