package Services;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class Methods {

    /*********************************************** CRYPTO FUNCTIONS ***********************************************/
    // Returns a hash of the data contained in the string
    public static byte[] hash(String data) {
        byte[] hash = null;
        byte[] hashContentBytes = stringToBytes(data);

        // make a hash of the byte array
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(hashContentBytes);
        }
        catch (Exception e) { e.printStackTrace(); }
        return hash;
    }

    // Returns a secret key
    public static SecretKey getSecretKey() {
        SecretKey secretKey = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            secretKey = keyGen.generateKey();
        }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        return secretKey;
    }

    // Returns a keypair with a private and a public key
    public static KeyPair getKeyPair() {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyGent = KeyPairGenerator.getInstance("DSA");
            keyPair = keyGent.generateKeyPair();
        }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        return keyPair;
    }

    // Returns a secret key based on some data input
    public static SecretKey KDF(String data) {
        SecretKey secretKey = null;
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
            // Create keySpec based on a password
            PBEKeySpec keySpec = new PBEKeySpec(data.toCharArray());
            secretKey = skf.generateSecret(keySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) { e.printStackTrace(); }
        return secretKey;
    }

    // Creates a signature based on the input data and a private key
    public static ArrayList<byte[]> getSignature(byte[] data, PrivateKey privateKey) {
        byte[] signature = null;
        try {
            // Create and initialize signature object
            Signature signEngine = Signature.getInstance("SHA256withDSA");
            signEngine.initSign(privateKey);

            // Sign data
            signEngine.update(data);
            signature = signEngine.sign();
        }
        catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) { e.printStackTrace(); }
        return new ArrayList<>(Arrays.asList(data, signature));
    }

    // Creates a signature based on the input data and a private key
    public static boolean checkSignature(ArrayList<byte[]> pair, PublicKey publicKey) {
        boolean valid = false;
        try {
            // Create and initialize signature object
            Signature signEngine = Signature.getInstance("SHA256withDSA");
            signEngine.initVerify(publicKey);

            // Check signature
            signEngine.update(pair.get(0));
            valid = signEngine.verify(pair.get(1));
        }
        catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) { e.printStackTrace(); }
        return valid;
    }
    /*********************************************** CRYPTO FUNCTIONS ***********************************************/


    /*********************************************** CONVERSIONS ***********************************************/
    // Join strings seperated by a ";"
    public static String joinStrings(String[] data) {
        String res = "";
        for (String s : data) {
            res+= s+";";
        }
        return res;
    }

    // Separate string based on ";" as separator
    public static String[] separateString(String data) {
        return data.split(";");
    }

    // Returns a byte array which represents the encoded string with the right encoding for signature
    public static byte[] stringToBytesSignatureEncoding(String data){return data.getBytes(StandardCharsets.UTF_8);}
    // Returns a byte array which represents the encoded string
    public static byte[] stringToBytes(String data) {
        return data.getBytes(StandardCharsets.UTF_8);
    }

    // Returns a string resulting from decoding the byte array
    public static String bytesToString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    // Returns a string resulting from formatting the LocalDateTime object
    public static String timeStampToString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }

    // Returns a LocalDateTime object resulting from formatting the string
    public static LocalDateTime stringToTimeStamp(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return LocalDateTime.parse(dateTime, formatter);
    }

    // Returns a string resulting from formatting the LocalDate object
    public static String dateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return date.format(formatter);
    }

    // Returns a LocalDate object resulting from formatting the string
    public static LocalDate stringToDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(date, formatter);
    }
    /*********************************************** CONVERSIONS ***********************************************/

}
