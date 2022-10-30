package Registrar;


import Facility.Facility;
import Interfaces.EnrollmentInterface;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.ArrayList;
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









}
