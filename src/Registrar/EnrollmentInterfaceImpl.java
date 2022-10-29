package Registrar;


import Interfaces.EnrollmentInterface;
import org.apache.commons.lang3.ArrayUtils;

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

    public EnrollmentInterfaceImpl() throws RemoteException {}


    /** Phase 1.1: Enrollment owner **/
    public void registerFacility(String CF) {
        List<byte[]> dayArray = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int nrOfDays = today.getMonth().length(LocalDate.EPOCH.isLeapYear());
        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDate day = firstDay;
        for (int i=0; i<nrOfDays; i++) {
            dayArray.add(day.toString().getBytes(StandardCharsets.UTF_8));
            day.plusDays(1);
        }

        List<SecretKey> keyArray = new ArrayList<>();
        List<byte[]> nymArray = new ArrayList<>();

        try {
            // Generate master secret key s
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            SecretKey s = keyGen.generateKey();

            // Generate secret key s_CF_dayi for each day of this month
            byte[] CFBytes = CF.getBytes(StandardCharsets.UTF_8);
            char[] keyChars = Base64.getEncoder().encodeToString(s.getEncoded()).toCharArray();

            // TODO weet niet of dit de ideale oplossing is als KDF
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            for (int i=0; i<nrOfDays; i++) {
                // use s as password, CF and day as salt
                byte[] salt = ArrayUtils.addAll(CFBytes, dayArray.get(i));
                PBEKeySpec keySpec = new PBEKeySpec(keyChars, salt, 5);
                SecretKey s_cf_dayi = skf.generateSecret(keySpec);
                keyArray.add(s_cf_dayi);
            }

            // Generate day-specific pseudonym nym_CF_dayi for each day of this month
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (int i=0; i<nrOfDays; i++){
                byte[] keyBytes = keyArray.get(i).getEncoded();
                byte[] temp = ArrayUtils.addAll(keyBytes, CFBytes);
                byte[] data = ArrayUtils.addAll(temp, dayArray.get(i));
                byte[] nym_cf_dayi = digest.digest(data);
                nymArray.add(nym_cf_dayi);
            }


        }
        catch (Exception e) { e.printStackTrace(); }

    }









}
