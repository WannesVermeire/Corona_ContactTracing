package Visitor;

import com.beust.ah.A;

import java.io.Serializable;
import java.util.ArrayList;

import static Services.Methods.*;

public class Visit implements Serializable {
    private String R_i;
    private String CF; //TODO CF niet opslaan?
    private ArrayList<byte[]> tokenPair;
    private String H;
    private String timeOfScan;

    public Visit(String R_i, String CF, String H, String timeOfScan) {
        this.R_i = R_i;
        this.CF = CF;
        this.H = H;
        this.timeOfScan = timeOfScan;
    }

    public String getR_i() {
        return R_i;
    }
    public String getCF() {
        return CF;
    }
    public String getH() {
        return H;
    }
    public String getScanTime() {
        return timeOfScan;
    }
    public void setTokenPair(ArrayList<byte[]> tokenPair) {
        this.tokenPair = tokenPair;
    }

    // Both of these methods ease conversion from en to string of visit object
    public Visit(String visit) {
        String[] data = separateString(visit);
        R_i = data[0];
//        String CFpart1 = data[1]; // Unique identifier of the facility
//        String CFpart2 = data[2];
//        String CFpart3 = data[3];
//        String CFpart4 = data[4];
//        CF = joinStrings(new String[]{CFpart1, CFpart2, CFpart3, CFpart4});
        byte[] tokenData = stringToBytes(data[1]);
        byte[] tokenSignature = stringToBytes(data[2]);
        tokenPair = new ArrayList<>();
        tokenPair.add(tokenData);
        tokenPair.add(tokenSignature);
        H = data[4];
        timeOfScan = data[4]; //todo index 5 werkt niet???


    }
    public String getLogString() {
        String tokenData = bytesToString(tokenPair.get(0));
        String tokenSignature = bytesToString(tokenPair.get(1));
        String[] data = new String[]{R_i, tokenData, tokenSignature, H, timeOfScan};
        return joinStrings(data);
    }



    @Override
    public String toString() {
        return "Visit{" +
                "R_i='" + R_i + '\'' +
                ", TokenPair='" + tokenPair + '\'' +
                ", Hash='" + H + '\'' +
                ", timeOfScan='" + timeOfScan + '\'' +
                '}';
    }
}
