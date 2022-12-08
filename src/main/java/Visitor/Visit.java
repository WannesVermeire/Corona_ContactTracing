package Visitor;

import java.io.Serializable;

import static Services.Methods.joinStrings;
import static Services.Methods.separateString;

public class Visit implements Serializable {
    private String R_i;
    private String CF;
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

    // Both of these methods ease conversion from en to string of visit object
    public Visit(String visit) {
        String[] data = separateString(visit);
        R_i = data[0];
        String CFpart1 = data[1]; // Unique identifier of the facility
        String CFpart2 = data[2];
        String CFpart3 = data[3];
        String CFpart4 = data[4];
        CF = joinStrings(new String[]{CFpart1, CFpart2, CFpart3, CFpart4});
        H = data[6];
        timeOfScan = data[7];
    }
    public String getLogString() {
        String[] data = new String[]{R_i, CF, H, timeOfScan};
        return joinStrings(data);
    }



    @Override
    public String toString() {
        return "Visit{" +
                "R_i='" + R_i + '\'' +
                ", CF='" + CF + '\'' +
                ", Hash='" + H + '\'' +
                ", timeOfScan='" + timeOfScan + '\'' +
                '}';
    }
}
