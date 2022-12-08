package Facility;

import Services.Methods;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.*;

import static Services.Methods.*;

public class Facility implements Serializable {

    private String id; // unique business number
    private String name;
    private String address;
    private String phoneNr;
    private List<SecretKey> keyArray;
    private List<byte[]> nymArray;
    private int[] randoms;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public Facility(String name, String address, String phoneNr) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
        this.phoneNr = phoneNr;
        KeyPair keyPair = getKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    public Facility(String id, String name, String address, String phoneNr, PublicKey publicKey) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNr = phoneNr;
        this.publicKey = publicKey;
    }

    // Gives back unique identifier CF
    public String getCF() {
        String[] data = {id, name, address, phoneNr};
        return Methods.joinStrings(data);
    }
    public PublicKey getPublicKey() {
        return publicKey;
    }
    public String getId() { return id; }
    public String getName() {
        return name;
    }
    public List<SecretKey> getKeyArray() {
        return keyArray;
    }

    /************************************* 1.1 FACILITY ENROLLMENT *************************************/
    public void setKeyArray(List<SecretKey> keyArray) {
        this.keyArray = keyArray;
    }
    public void setNymArray(List<byte[]> nymArray) {
        this.nymArray = nymArray;
    }
    // Returns arraylist containing the original data and the signature
    public ArrayList<byte[]> generateCFWithSignature() {
        byte[] data = stringToBytes(getCF());
        return getSignature(data, privateKey);
    }
    public void generateRandoms() {
        // Get the nrOfDays of month
        LocalDate today = LocalDate.now();
        int nrOfDays = today.getMonth().length(LocalDate.EPOCH.isLeapYear());
        Random rand = new Random();
        randoms = new int[nrOfDays];
        int Ri;
        for (int i=0; i<nrOfDays; i++) {
            Ri = rand.nextInt(Integer.MAX_VALUE);
            randoms[i] = Ri;
        }
    }
    public void calculateQRCodes() {
        String CF = getCF();
        for (int i=0; i<nymArray.size(); i++) {
            String Ri = Integer.toString(randoms[i]);
            String nym_CF_dayi = bytesToString(nymArray.get(i));

            // Create hash of R_i and nym_CF_dayi
            String[] data = {Ri, nym_CF_dayi};
            String dataString = joinStrings(data);
            byte[] hash = hash(dataString);

            // Create content of QR code
            String hashString = bytesToString(hash);
            String[] data2 = {Ri, CF, hashString};
            String barcodeText = joinStrings(data2);

            // Create QR code
            String filename = "QRCodes/QRCode_day"+(i+1)+".jpg";
            try {
                QRCodeWriter barcodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);

                BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
                File outputfile = new File(filename);
                ImageIO.write(bufferedImage, "jpg", outputfile);

            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }
    /************************************* 1.1 FACILITY ENROLLMENT *************************************/

    @Override
    public String toString() {
        String nymarray = "";
        if(nymArray!=null)
            for (byte[] arr : nymArray)
                nymarray+= Arrays.toString(arr)+",";
        String keyarr = "";
        if(keyArray!=null)
            for (SecretKey arr : keyArray)
                keyarr+= arr.toString()+",";

        return "facility{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNr='" + phoneNr + '\'' +
                ", keyArray=" + keyarr +
                ", nymArray=" + nymarray + "}";
    }

    public String toGUIString() {
        String nymarray = "";
        if(nymArray!=null)
            for (byte[] arr : nymArray)
                nymarray+= Arrays.toString(arr)+",";
        String keyarr = "";
        if(keyArray!=null)
            for (SecretKey arr : keyArray)
                keyarr+= arr.toString()+",";

        return "facility{" + "\n" +
                "id='" + id + '\'' + "\n" +
                ", name='" + name + '\'' + "\n" +
                ", address='" + address + '\'' + "\n" +
                ", phoneNr='" + phoneNr + '\'' + "\n" +
                ", keyArray=" + keyarr + "\n" +
                ", nymArray=" + nymarray + "}";
    }

}
