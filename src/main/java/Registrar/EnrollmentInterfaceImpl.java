package Registrar;


import Facility.Facility;
import Interfaces.EnrollmentInterface;
import Visitor.Visitor;

import static Services.Methods.*;
import javax.crypto.SecretKey;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.time.LocalDate;
import java.util.*;

//Interface which facilities and visitors can use to enroll in the registrar
public class EnrollmentInterfaceImpl extends UnicastRemoteObject implements EnrollmentInterface {
    private final int INCUBATION_DAYS = 10;
    private SecretKey s;
    private RegistrarDB registrarDB;

    public EnrollmentInterfaceImpl(SecretKey s, RegistrarDB registrarDB) throws RemoteException {
        this.s = s;
        this.registrarDB = registrarDB;
    }

    public int getINCUBATION_DAYS() { return INCUBATION_DAYS; }



    /**************************************** FACILITIES ****************************************/
    // Verify signature on CF and add to database if correct
    public void registerFacility(String CF, PublicKey publicKey, byte[] signature) {
        Facility facility;
        byte[] data = stringToBytes(CF);
        boolean valid = checkSignature(data, signature, publicKey);

        if (valid) {
            String[] CFcontent = separateString(CF);
            facility = new Facility(CFcontent[0], CFcontent[1], CFcontent[2], CFcontent[3], publicKey);
            registrarDB.addFacility(facility);
            System.out.println("Added tot database.: "+facility);
        }
        else {
            System.out.println("The provided CF was not from a reliable source.");
        }

    }

    // Generate secret key s_CF_dayi for each day of this month
    public List<SecretKey> getKeyArray(String id)  {
        List<SecretKey> keyArray = new ArrayList<>();
        Facility facility = registrarDB.findFacilityById(id);

        // Get the first day of the month and nrOfDays of month
        LocalDate today = LocalDate.now();
        int nrOfDays = today.getMonth().length(LocalDate.EPOCH.isLeapYear());
        LocalDate day = today.withDayOfMonth(1);

        String key = s.toString();
        String CF = facility.getCF();

        for (int i=0; i<nrOfDays; i++) {
            String dayi = dateToString(day);
            String[] data = {key, CF, dayi};
            String dataString = joinStrings(data);
            SecretKey s_cf_day_i = KDF(dataString);

            keyArray.add(s_cf_day_i);
            day = day.plusDays(1);
        }
        facility.setKeyArray(keyArray);
        return keyArray;
    }

    // Generate day-specific pseudonym nym_CF_dayi for each day of this month
    public List<byte[]> getNymArray(String id) {
        List<byte[]> nymArray = new ArrayList<>();
        Facility facility = registrarDB.findFacilityById(id);
        List<SecretKey> keyArray = facility.getKeyArray();

        // Get the first day of the month and nrOfDays of month
        LocalDate today = LocalDate.now();
        int nrOfDays = today.getMonth().length(LocalDate.EPOCH.isLeapYear());
        LocalDate day = today.withDayOfMonth(1);

        String CF = facility.getCF();
        for (int i = 0; i < nrOfDays; i++) {
            String key = keyArray.get(i).toString();
            String dayi = dateToString(day);
            String[] data = {key, CF, dayi};
            String dataString = joinStrings(data);
            byte[] nym_cf_dayi = hash(dataString);

            nymArray.add(nym_cf_dayi);
            day = day.plusDays(1);
        }
        return nymArray;

    }
    /**************************************** FACILITIES ****************************************/


    /**************************************** VISITOR ****************************************/
    public Visitor registerVisitor(Visitor visitor) {
        registrarDB.addVisitor(visitor);
        generateTokens(visitor);
        return visitor;
    }

    private void generateTokens(Visitor visitor) {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            PrivateKey key = registrarDB.getPrivate();
            List<byte[]>[] signedtokenList = new List[31];
            List<byte[]> signedtokenListPerDay;

            List<byte[]>[] unsignedtokenList = new List[31];
            List<byte[]> unsignedtokenListPerDay;

            LocalDate date = LocalDate.now();
            for(int j=0; j<31; j++) {
                signedtokenListPerDay = new ArrayList<>();
                unsignedtokenListPerDay = new ArrayList<>();
                for (int i = 0; i < 48; i++) {
                    String[] dataStrings = new String[]{Long.toString(random.nextLong()), dateToString(date)};
                    String dataString = joinStrings(dataStrings);
                    byte[] data = stringToBytes(dataString);
                    signedtokenListPerDay.add(getSignature(data, key));
                    unsignedtokenListPerDay.add(data);
                    date.plusDays(1);
                }
                signedtokenList[j] = signedtokenListPerDay;
                unsignedtokenList[j] = unsignedtokenListPerDay;

                visitor.setSignedTokens(signedtokenList);
                visitor.setUnsignedTokens(unsignedtokenList);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }
    /**************************************** VISITOR ****************************************/
}
