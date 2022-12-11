package Visitor;


import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import static Services.Methods.*;

public class Visit implements Serializable {
    private String R_i;
    private String CF;
    private ArrayList<byte[]> tokenPair;
    private String H;
    private String[] timelogs;

    public Visit(String R_i, String CF, String H, String timeOfScan) {
        this.R_i = R_i;
        this.CF = CF;
        this.H = H;
        this.timelogs = new String[]{timeOfScan};
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
        return timelogs[0];
    }
    public String getExitTime() {
        return timelogs[timelogs.length-1];
    }
    public ArrayList<byte[]> getTokenPair() {
        return tokenPair;
    }
    public void setTokenPair(ArrayList<byte[]> tokenPair) {
        this.tokenPair = tokenPair;
    }

    // Both of these methods ease conversion from en to string of visit object
    public Visit(String visit) {
        String[] data = separateString(visit);
        R_i = data[0];
        String CFpart1 = data[1];
        String CFpart2 = data[2];
        String CFpart3 = data[3];
        String CFpart4 = data[4];
        CF = joinStrings(new String[]{CFpart1, CFpart2, CFpart3, CFpart4});
        byte[] tokenData = stringToHash(data[6]);
        byte[] tokenSignature = stringToHash(data[7]);
        tokenPair = new ArrayList<>();
        tokenPair.add(tokenData);
        tokenPair.add(tokenSignature);
        H = data[8];
        timelogs = new String[data.length-9];
        for(int i =9; i < data.length; i++) {
            timelogs[i-9] = data[i];
        }


    }
    public String getLogString() {
        String tokenData = hashToString(tokenPair.get(0));
        String tokenSignature = hashToString(tokenPair.get(1));
        String times = joinStrings(timelogs);
        String[] data = new String[]{R_i, CF, tokenData, tokenSignature, H, times.substring(0, times.length()-1)};
        return joinStrings(data);
    }

    @Override
    public String toString() {
        String token = "null";
        String tokenSignature = "null";
        if (tokenPair!=null) {
            token = hashToString(tokenPair.get(0));
            tokenSignature = hashToString(tokenPair.get(1));
        }
        return "Visit{" +
                "R_i='" + R_i + '\'' +
                ", CF='" + CF + '\'' +
                ", token=" + token +
                ", tokenSignature=" + tokenSignature +
                ", H='" + H + '\'' +
                ", scanTimes='" + Arrays.toString(timelogs) + '\'' +
                '}';
    }
    
    public String toGUIString(){String token = "null";
        String tokenSignature = "null";
        if (tokenPair!=null) {
            token = hashToString(tokenPair.get(0));
            tokenSignature = hashToString(tokenPair.get(1));
        }
        return "Visit{" + "\n" +
                "R_i='" + R_i + '\'' + "\n" +
                ", CF='" + CF + '\'' + "\n" +
                ", token=" + token +
                ", tokenSignature=" + tokenSignature +
                ", H='" + H + '\'' + "\n" +
                ", scanTimes='" + Arrays.toString(timelogs) + '\'' +
                '}';
    }

    public void updateTimeStamp(String token, String timestamp) {
        String [] old = timelogs;
        timelogs = new String[old.length +1];
        for(int i = 0; i<old.length; i++) {
            timelogs[i] = old[i];
        }
        timelogs[old.length] = timestamp;
    }
}
