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
    public MixingProxyInterfaceImplementation(MatchingServiceInterface impl) throws RemoteException, NotBoundException {
        this.impl = impl;
    }

    /** 2.1 Visit facility **/
    @Override
    public void sendCapsule(Visitor visitor, Capsule capsule, byte[] token) throws Exception {
        Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
        dsa.initVerify(visitor.getPublicKey());
        // 3 checks: signature, day, not yet used
        System.out.println("Token : " + new String(capsule.getToken(), StandardCharsets.UTF_8));
        if(dsa.verify(token)) {
            if(true) {
                if(impl.isTokenUsed(capsule.getToken())) {
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
