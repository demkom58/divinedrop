package com.demkom58.divinedrop;

public class DataContainer {

    private int timer;
    private String format;

    public DataContainer(int timer, String format) {
        this.timer = timer;
        this.format = format;
    }

    public int getTimer() {
        return timer;
    }

    public String getFormat() {
        return format;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void setFormat( String format) {
        this.format = format;
    }
}
