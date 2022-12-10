package MixingProxy;

import Interfaces.MatchingServiceInterface;
import Interfaces.MixingProxyInterface;
import Interfaces.RegistrarInterface;
import Visitor.Visit;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Services.Methods.*;

public class MixingProxyInterfaceImpl extends UnicastRemoteObject implements MixingProxyInterface {

    private MatchingServiceInterface impl;
    private MixingProxyDB mixingProxyDB;

    public MixingProxyInterfaceImpl() throws RemoteException, NotBoundException {
        this.mixingProxyDB = new MixingProxyDB();
        impl = connectToMatchingService();
    }


    @Override
    // If all checks on the capsule data are correct we return a confirmation: sign(token)
    public ArrayList<byte[]> verifyAndSendConfirmation(Visit visit, PublicKey publicKey) throws Exception {
         // 3 checks: signature, day, not yet used
        byte[] token = visit.getTokenPair().get(0);

        if(!checkSignature(visit.getTokenPair(), publicKey))
            throw new SignatureException("Invalid signature");

        if(!verifyTokenDay(visit.getScanTime(), token))
            throw new Exception("Date of token is not correct");

        if(impl.containsToken(token) || mixingProxyDB.containsCapsule(token))
            throw new Exception("Token is already used");

        System.out.println("Mixing Proxy: Capsule is valid!");

        // Save capsule
        mixingProxyDB.addCapsule(hashToString(token), visit);

        // Create confirmation
        updateTimeStamp(hashToString(token), visit.getScanTime());

        // Save this capsule
        mixingProxyDB.addCapsule(visit);

        return getSignature(stringToHash(visit.getH()), mixingProxyDB.getSecretKey());
    }

    private boolean verifyTokenDay(String scanTime, byte[] bytes) {
        int currentDay = stringToTimeStamp(scanTime).getDayOfMonth();
        String tokenDay_String = separateString(bytesToString(bytes))[1];
        int tokenDay = stringToDate(tokenDay_String).getDayOfMonth();
        return currentDay == tokenDay;
    }
    @Override
    public PublicKey getPublicKey() {
        return mixingProxyDB.getPublicKey();
    }

    @Override
    // Stuur alles door naar de MatchingService
    public void flushCache() throws RemoteException {
        System.out.println("Begin flushen");
        while(!mixingProxyDB.isEmptyCapsules()) {
            String randomToken = mixingProxyDB.getRandomTokenCapsule();
            impl.addCapsule(randomToken, mixingProxyDB.getCapsule(randomToken));
            mixingProxyDB.removeToken(randomToken);
        }
        while(!mixingProxyDB.isEmptyTimeStamps()) {
            String randomToken = mixingProxyDB.getRandomTokenTime();
            impl.addTimeStamps(randomToken, mixingProxyDB.getTimeStamp(randomToken));
            mixingProxyDB.removeTimeStamp(randomToken);
        }
    }

    @Override
    public void updateTimeStamp(String token, String timeStamp) throws RemoteException {
        mixingProxyDB.updateTimeStamp(token, timeStamp);
    }

    @Override
    public void notifyNonInformed(List<Entry> nonInformed) throws RemoteException {
        try {
            System.out.println("These people were not yet informed");
            RegistrarInterface registrar = connectToRegistrar();
            for(Entry entry : nonInformed) {
                System.out.println("TelNr: " + registrar.getTelNrUser(entry.getToken()));
            }
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }

    }
}
