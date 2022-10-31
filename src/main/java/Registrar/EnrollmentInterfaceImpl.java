package Registrar;


import Facility.Facility;
import Interfaces.EnrollmentInterface;
import Visitor.Visitor;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;


public class EnrollmentInterfaceImpl extends UnicastRemoteObject implements EnrollmentInterface {

    private RegistrarDB registrarDB;

    public EnrollmentInterfaceImpl(RegistrarDB registrarDB) throws RemoteException {
        this.registrarDB = registrarDB;
    }


    /** Phase 1.1: Enrollment owner **/
    // Generate secret key s_CF_dayi for each day of this month
    public void registerFacility(Facility facility) {

        registrarDB.addFacility(facility);

        LocalDate today = LocalDate.now();
        int nrOfDays = today.getMonth().length(LocalDate.EPOCH.isLeapYear());
        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDate day = firstDay;

        List<SecretKey> keyArray = new ArrayList<>();

        try {
            // Generate master secret key s
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            SecretKey s = keyGen.generateKey();

            byte[] CFBytes = facility.getCF().getBytes(StandardCharsets.UTF_8);
            char[] keyChars = Base64.getEncoder().encodeToString(s.getEncoded()).toCharArray();

            // TODO weet niet of dit de ideale oplossing is als KDF
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            for (int i=0; i<nrOfDays; i++) {
                // use s as password, CF and day as salt
                byte[] dayBytes = day.toString().getBytes(StandardCharsets.UTF_8);
                byte[] salt = ArrayUtils.addAll(CFBytes, dayBytes);
                PBEKeySpec keySpec = new PBEKeySpec(keyChars, salt, 5, 10);
                SecretKey s_cf_dayi = skf.generateSecret(keySpec);
                keyArray.add(s_cf_dayi);
                day = day.plusDays(1);
            }

            facility.setKeyArray(keyArray);
            System.out.println(keyArray);

        }
        catch (Exception e) { e.printStackTrace(); }

    }

    // Generate day-specific pseudonym nym_CF_dayi for each day of this month
    public List<byte[]> getNymArray(int id) {

        Facility facility = registrarDB.findFacilityById(id);

        List<byte[]> nymArray = new ArrayList<>();
        List<SecretKey> keyArray = facility.getKeyArray();
        byte[] CFBytes = facility.getCF().getBytes(StandardCharsets.UTF_8);

        LocalDate today = LocalDate.now();
        int nrOfDays = today.getMonth().length(LocalDate.EPOCH.isLeapYear());
        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDate day = firstDay;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (int i = 0; i < nrOfDays; i++) {
                byte[] dayBytes = day.toString().getBytes(StandardCharsets.UTF_8);
                byte[] keyBytes = keyArray.get(i).getEncoded();
                byte[] temp = ArrayUtils.addAll(keyBytes, CFBytes);
                byte[] data = ArrayUtils.addAll(temp, dayBytes);
                byte[] nym_cf_dayi = digest.digest(data);
                nymArray.add(nym_cf_dayi);
                day = day.plusDays(1);
            }
        }
        catch (Exception e) { e.printStackTrace(); }

        return nymArray;
    }

    public void registerVisitor(Visitor visitor) {
        registrarDB.addVisitor(visitor);
        List<byte[]> tokens = new ArrayList<>();
        List<byte[]> controle = new ArrayList<>();

        byte[] today = LocalDate.now().toString().getBytes(StandardCharsets.UTF_8);
        try {
            // TODO: wat wil sign_RC zeggen? Keymanagement?
            Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            keyGen.initialize(1024, random);
            KeyPair pair = keyGen.generateKeyPair();

            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();

            dsa.initSign(privateKey);

            byte[] signed;
            // TODO is dit wat bedoelt wordt met die random.... ik vrees er wat voor...
            for (int i=0; i<48; i++) {
                // Wannes manier...
                // byte[] data = ArrayUtils.addAll(Long.toString(random.nextLong()).getBytes(StandardCharsets.UTF_8), today);
                // dsa.update(data);
                // TODO
                // Doordat de random al in het keypair zit, denk ik niet dat het noodzakelijk is om deze hierin nog eens te steken...
                // Dus kan/moet het volgens mij zo...
                // Heb vorige laten staan om alternatief aan te tonen...
                dsa.update(today);
                tokens.add(dsa.sign());
            }

            // ***** Controle om aan te tonen dat mijn methode werkt ******
            dsa.initVerify(publicKey);
            byte[] previous = null;
            for(byte[] d : tokens) {
                dsa.update(today);
                if(dsa.verify(d) && !Arrays.equals(d, previous)) System.out.println("Very Nice");
                else if(Arrays.equals(d, previous)) System.out.println("Redeneerfout");
                else System.out.println("programmeerfout");
            }
            // ***** Controle om aan te tonen dat mijn methode werkt ******

            visitor.setTokens(tokens);
        }
        catch (Exception e) { e.printStackTrace(); }
    }











}
