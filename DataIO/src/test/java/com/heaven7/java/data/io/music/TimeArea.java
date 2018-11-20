package com.heaven7.java.data.io.music;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** @author heaven7 */
public class TimeArea implements Comparable<TimeArea>{

    private float begin;
    private float end;

    public float getBegin() {
        return begin;
    }

    public void setBegin(float begin) {
        this.begin = begin;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
    }

    public List<Float> asList(){
        return Arrays.asList(begin, end);
    }

    @Override
    public int compareTo(TimeArea o) {
        return Float.compare(begin, o.begin);
    }
}
