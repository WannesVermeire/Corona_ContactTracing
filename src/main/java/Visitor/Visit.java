package Visitor;

import java.io.Serializable;

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
}
