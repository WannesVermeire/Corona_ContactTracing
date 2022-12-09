package MixingProxy;

import java.time.LocalDateTime;
import java.util.Arrays;

public class Entry {
    private byte[] token; // links the user
    private byte[] hash; // links the facility
    private boolean critical;
    private boolean informed;
    LocalDateTime beginTimeWindow;
    LocalDateTime endTimeWindow;

    public Entry(byte[] token, byte[] hash) {
        this.token = token;
        this.hash = hash;
    }

    public Entry(byte[] token, byte[] hash, LocalDateTime beginTimeWindow, LocalDateTime endTimeWindow) {
        this.token = token;
        this.hash = hash;
        this.beginTimeWindow = beginTimeWindow;
        this.endTimeWindow = endTimeWindow;
    }

    public byte[] getToken() {
        return token;
    }
    public byte[] getHash() {
        return hash;
    }
    public boolean isCritical() {
        return critical;
    }
    public boolean isInformed() {
        return informed;
    }
    public LocalDateTime getBeginTimeWindow() {
        return beginTimeWindow;
    }
    public LocalDateTime getEndTimeWindow() {
        return endTimeWindow;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }
    public void setInformed(boolean informed) {
        this.informed = informed;
    }
    public void setBeginTimeWindow(LocalDateTime beginTimeWindow) {
        this.beginTimeWindow = beginTimeWindow;
    }
    public void setEndTimeWindow(LocalDateTime endTimeWindow) {
        this.endTimeWindow = endTimeWindow;
    }




    @Override
    public String toString() {
        return "Entry{" +
                "token=" + Arrays.toString(token) +
                ", hash=" + Arrays.toString(hash) +
                ", critical=" + critical +
                ", informed=" + informed +
                ", beginTimeWindow=" + beginTimeWindow +
                ", endTimeWindow=" + endTimeWindow +
                '}';
    }
}
