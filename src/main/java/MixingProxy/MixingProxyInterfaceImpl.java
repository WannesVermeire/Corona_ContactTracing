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

import static Services.Methods.*;

public class MixingProxyInterfaceImpl extends UnicastRemoteObject implements MixingProxyInterface {
    private MatchingServiceInterface impl;
    private MixingProxyDB mixingProxyDB;
    public MixingProxyInterfaceImpl() throws RemoteException, NotBoundException {
        // matchingRegistry is a reference (stub) for the registry that is running on port 2300 a.k.a. the MatchingService
        Registry matchingRegistry = LocateRegistry.getRegistry("localhost", 2300);
        // Obtain the stub for the remote object with name "MatchingService" a.k.a. the MatchingServiceInterfaceImplementation
        this.impl = (MatchingServiceInterface) matchingRegistry.lookup("MatchingService");
        this.mixingProxyDB = new MixingProxyDB();
    }

    /** 2.1 Visit facility **/
    @Override
    public ArrayList<byte[]> verifyAndSignCapsule(Visitor visitor, PublicKey publicKey, String scanTime, ArrayList<byte[]> tokenPair, byte[] hashValue) throws Exception {
        // 3 checks: signature, day, not yet used
        byte[] token = tokenPair.get(0);
        if(checkSignature(tokenPair, publicKey)) {
            if(verifyTokenDay(scanTime, token)) {
                if(!impl.containsToken(token) && !mixingProxyDB.containsToken(token)) {
                    System.out.println("Capsule is valid!");
                    String[] capsuleArr = new String[] {bytesToString(token), scanTime, bytesToString(hashValue)};
                    String capsule = joinStrings(capsuleArr);
                    mixingProxyDB.cacheCapsule(token, capsule);
                    return signCapsule(capsule);
                }
                else {
                    throw new Exception("Token is already used");
                }
            }
            else {
                throw new Exception("Date of token is not correct");
            }
        }
        else {
            throw new SignatureException("Invalid signature");
        }
    }

    private boolean verifyTokenDay(String scanTime, byte[] bytes) {
        int currentDay = stringToDate(scanTime).getDayOfMonth();
        String tokenDay_String = separateString(bytesToString(bytes))[1];
        int tokenDay = stringToDate(tokenDay_String).getDayOfMonth();
        return currentDay == tokenDay;
    }
    @Override
    public PublicKey getPublicKey() {
        return mixingProxyDB.getPublicKey();
    }
    public ArrayList<byte[]> signCapsule(String capsule) {
        return mixingProxyDB.signCapsule(capsule);
    }
}
