package com.heaven7.java.data.io.bean;

import java.util.Arrays;
import java.util.List;

/** @author heaven7 */
public class TimeArea implements Comparable<TimeArea>{

    private float begin;
    private float end;

    private String rawBegin;
    private String rawEnd;

    public String getRawBegin() {
        return rawBegin;
    }
    public void setRawBegin(String rawBegin) {
        this.rawBegin = rawBegin;
    }

    public String getRawEnd() {
        return rawEnd;
    }
    public void setRawEnd(String rawEnd) {
        this.rawEnd = rawEnd;
    }

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

    public String toMappingText(){
        return "[ " + begin + " ~~~ " + end + " ] -----> [ "
                + rawBegin + " ~~~ " + rawEnd + " ]";
    }

    public List<Float> asList(){
        return Arrays.asList(begin, end);
    }

    @Override
    public int compareTo(TimeArea o) {
        return Float.compare(begin, o.begin);
    }
}
