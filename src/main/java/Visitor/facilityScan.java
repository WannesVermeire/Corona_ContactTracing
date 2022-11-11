package Visitor;

public class facilityScan {
    private String R_i;
    private String CF;
    private String H;
    private String scanDate;

    public facilityScan(String R_i, String CF, String H, String scanDate) {
        this.R_i = R_i;
        this.CF = CF;
        this.H = H;
        this.scanDate = scanDate;
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

    public String getScanDate() {
        return scanDate;
    }
}
