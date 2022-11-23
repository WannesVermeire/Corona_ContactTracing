package MixingProxy;

import java.io.Serializable;
import java.util.Calendar;

public class Capsule implements Serializable {
    private int timeInterval;
    private byte[] token;
    private String H;

    public int getCurrrent_dateTime() {
        return timeInterval;
    }

    public byte[] getToken() {
        return token;
    }

    public String getH() {
        return H;
    }

    public Capsule(int timeInterval, byte[] token, String h) {
        this.timeInterval = timeInterval;
        this.token = token;
        H = h;
    }
}
