package Registrar;

import Facility.Facility;
import Interfaces.EnrollmentInterface;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EnrollmentInterfaceImpl extends UnicastRemoteObject implements EnrollmentInterface {

    public EnrollmentInterfaceImpl() throws RemoteException {}


    /** Phase 1.1: Enrollment owner **/
    public void registerFacility(String CF) {
        try {
            // Generate master secret key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            SecretKey sKey = keyGen.generateKey();

            // Generate secret key s_CF_dayi for each day of this month
            //todo
            //  -- hieronder dus met het resultaat hiervan werken ipv sKey


            // Generate day-specific pseudonym nym_CF_dayi for each day of this month
            List<byte[]> nymArray = new ArrayList<>();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            LocalDate day = LocalDate.now();
            int nrOfDays = day.getMonth().length(LocalDate.EPOCH.isLeapYear());
            day = day.withDayOfMonth(1);

            byte[] keyArray = sKey.getEncoded();
            byte[] CFArray = CF.getBytes(StandardCharsets.UTF_8);
            byte[] dayArray;

            for (int i=0; i<nrOfDays; i++){
                dayArray = day.toString().getBytes(StandardCharsets.UTF_8);
                byte[] temp = ArrayUtils.addAll(keyArray, CFArray);
                byte[] data = ArrayUtils.addAll(temp, dayArray);

                byte[] nym = digest.digest(data);
                day = day.plusDays(1);
                nymArray.add(nym);
            }



/*
            // Maak encryptie algoritme
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sKey);
            // Encrypt
           // byte[] cipherText = cipher.doFinal(bytesPerson1);
            System.out.println("Encrypted data: "+Arrays.toString(cipherText));

            // Maak decrypte algoritme
            cipher.init(Cipher.DECRYPT_MODE, sKey);
            // Decrypt
            byte[] plainText = cipher.doFinal(cipherText);
            System.out.println("Decrypted data: "+Arrays.toString(plainText));
            System.out.println();

 */

        }
        catch (Exception e) { e.printStackTrace(); }

    }









}
