package dabbiks.uhc.utils;

public class TimeUtils {

    private long time = 0;

    public void incrementTime() {
        time++;
    }

    public long getTime() {
        return time;
    }

    public String getFormattedTime() {
        long minutes = time / 60;
        long seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}
