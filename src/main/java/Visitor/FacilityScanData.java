package Visitor;

import java.util.Calendar;

public class FacilityScanData {
    private String R_i;
    private String CF;
    private String H;
    private Calendar current_dateTime;

    public FacilityScanData(String R_i, String CF, String H, Calendar current_dateTime) {
        this.R_i = R_i;
        this.CF = CF;
        this.H = H;
        this.current_dateTime = current_dateTime;
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

    public Calendar getScanDay() {
        return current_dateTime;
    }
}
