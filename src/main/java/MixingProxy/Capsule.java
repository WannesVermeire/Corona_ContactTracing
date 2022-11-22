package MixingProxy;

import java.io.Serializable;
import java.util.Calendar;

public class Capsule implements Serializable {
    private Calendar currrent_dateTime;
    private byte[] token;
    private String H;

    public Calendar getCurrrent_dateTime() {
        return currrent_dateTime;
    }

    public byte[] getToken() {
        return token;
    }

    public String getH() {
        return H;
    }

    public Capsule(Calendar currrent_dateTime, byte[] token, String h) {
        this.currrent_dateTime = currrent_dateTime;
        this.token = token;
        H = h;
    }
}
