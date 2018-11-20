package com.heaven7.java.data.io.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author heaven7
 */
public class MusicItem {

    @Expose
    private String id = "default";
    @Expose
    private String name;
    @Expose
    private List<String> domains;

    @Expose
    private List<Float> times;
    @Expose
    @SerializedName("property")
    private int matter;
    @Expose
    private int rhythm;

    @Expose
    private List<List<Float>> slow_speed_areas;
    @Expose
    private List<List<Float>> middle_speed_areas;
    @Expose
    private List<List<Float>> high_speed_areas;

    @Expose(serialize = false, deserialize = false)
    private int rowIndex;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<String> getDomains() {
        return domains;
    }
    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public List<Float> getTimes() {
        return times;
    }

    public void setTimes(List<Float> times) {
        this.times = times;
    }

    public int getMatter() {
        return matter;
    }

    public void setMatter(int matter) {
        this.matter = matter;
    }

    public int getRhythm() {
        return rhythm;
    }

    public void setRhythm(int rhythm) {
        this.rhythm = rhythm;
    }

    public List<List<Float>> getSlow_speed_areas() {
        return slow_speed_areas;
    }

    public void setSlow_speed_areas(List<List<Float>> slow_speed_areas) {
        this.slow_speed_areas = slow_speed_areas;
    }

    public List<List<Float>> getMiddle_speed_areas() {
        return middle_speed_areas;
    }

    public void setMiddle_speed_areas(List<List<Float>> middle_speed_areas) {
        this.middle_speed_areas = middle_speed_areas;
    }

    public List<List<Float>> getHigh_speed_areas() {
        return high_speed_areas;
    }

    public void setHigh_speed_areas(List<List<Float>> high_speed_areas) {
        this.high_speed_areas = high_speed_areas;
    }

    public void setLineNumber(int rowIndex) {
        this.rowIndex = rowIndex;
    }
    public int getLineNumber(){
        return rowIndex;
    }
}
