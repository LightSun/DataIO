package com.heaven7.java.data.io.bean;

import java.util.List;

/**
 * @author heaven7
 */
public class CutInfo {

    public static final byte TYPE_SPARSE         =  1;
    public static final byte TYPE_INTENSIVE      =  1 << 1;
    public static final byte FLAG_TRANSITION_CUT = 1 << 2;

    private byte type;
    private List<Float> cuts;

    public byte getType() {
        return type;
    }
    public void setType(byte type) {
        this.type = type;
    }

    public List<Float> getCuts() {
        return cuts;
    }
    public void setCuts(List<Float> cuts) {
        this.cuts = cuts;
    }

    public Float getMaxTime() {
        return cuts.get(cuts.size() - 1);
    }
}
