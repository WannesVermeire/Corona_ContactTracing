package Visitor;

public class FacilityScanData {
    private String R_i;
    private String CF;
    private String H;
    private int scanDay;
    private int scanMonth;
    private int scanYear;
    private int scanHour;
    private int scanMin;

    public FacilityScanData(String R_i, String CF, String H, int scanDay, int scanMonth, int scanYear, int scanHour, int scanMin) {
        this.R_i = R_i;
        this.CF = CF;
        this.H = H;
        this.scanDay = scanDay;
        this.scanMonth = scanMonth;
        this.scanYear = scanYear;
        this.scanHour = scanHour;
        this.scanMin = scanMin;
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

    public int getScanDay() {
        return scanDay;
    }
}
