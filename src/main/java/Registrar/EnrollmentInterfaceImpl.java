package Registrar;


import Facility.Facility;
import Interfaces.EnrollmentInterface;
import Visitor.Visitor;
import org.apache.commons.lang3.ArrayUtils;

import static Services.Methods.*;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
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


    /** Phase 1.1: Catering facility enrollment **/

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
    public List<SecretKey> getKeyArray(String id) {
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

    /** 1.2 + 1.3 Enroll visitor + retrieve tokens **/
    public Visitor registerVisitor(Visitor visitor) {
        registrarDB.addVisitor(visitor);
        //TODO Aparte methodes?

        byte[] today = LocalDate.now().toString().getBytes(StandardCharsets.UTF_8);
        try {
            // TODO: wat wil sign_RC zeggen? Keymanagement?
            Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            keyGen.initialize(1024, random);
            KeyPair pair = keyGen.generateKeyPair();
            visitor.setKeys(pair);

            PrivateKey privateKey = pair.getPrivate();

            dsa.initSign(pair.getPrivate());

            List<byte[]>[] tokenList = new List[31];
            List<byte[]> tokenListPerDay;
            // TODO is dit wat bedoelt wordt met die random.... ik vrees er wat voor...
            for(int j=0; j<30; j++) {
                tokenListPerDay = new ArrayList<>();
                for (int i = 0; i < 48; i++) {
                    /* Wannes manier...*/
                    byte[] data = ArrayUtils.addAll(Long.toString(random.nextLong()).getBytes(StandardCharsets.UTF_8), (byte) ';', (byte) (j+1));
                    dsa.update(data);
                    tokenListPerDay.add(dsa.sign());
                    /*  TODO
                    Doordat de random al in het keypair zit, denk ik niet dat het noodzakelijk is om deze hierin nog eens te steken...
                    Dus kan/moet het volgens mij zo...
                    Heb vorige laten staan om alternatief aan te tonen...

                    dsa.update(today) */

                }
                tokenList[j] = tokenListPerDay;
            }
            visitor.setTokens(tokenList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return visitor;
    }

}
