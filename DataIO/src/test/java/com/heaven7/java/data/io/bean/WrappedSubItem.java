package com.heaven7.java.data.io.bean;

/**
 * @author heaven7
 */
public class WrappedSubItem<T> {

    private T subItem;
    private String name;
    private int duration;
    private String rawName;
    private int lineNumber;

    public WrappedSubItem(T subItem, String name, int duration, String rawName, int lineNumber) {
        this.subItem = subItem;
        this.name = name;
        this.duration = duration;
        this.rawName = rawName;
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getRawName() {
        return rawName;
    }
    public void setRawName(String rawName) {
        this.rawName = rawName;
    }

    public T getSubItem() {
        return subItem;
    }
    public void setSubItem(T subItem) {
        this.subItem = subItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
