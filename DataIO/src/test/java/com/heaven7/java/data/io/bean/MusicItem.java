package com.heaven7.java.data.io.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.data.io.music.Configs;

import java.util.List;

/**
 * @author heaven7
 */
public class MusicItem implements MusicItemDelegate{

    @Expose
    private String id = "default";
    @Expose(serialize = false)
    private String name;
    @Expose
    private List<String> domains;

    @Expose
    private int duration; //in seconds

    @Expose
    private List<Float> times;
    @Expose
    @SerializedName("property")
    private int property;
    @Expose
    private int rhythm;

    @Expose
    private List<List<Float>> slow_speed_areas;
    @Expose
    private List<List<Float>> middle_speed_areas;
    @Expose
    private List<List<Float>> high_speed_areas;

    //----------------------------------------------------

    @Expose(serialize = false, deserialize = false)
    private int rowIndex;
    @Expose(serialize = false)
    private String filePath;

    @Expose(serialize = false)
    private String categoryStr;
    @Expose(serialize = false)
    private int category;

    @Override
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

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


    @Override
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

    @Override
    public int getProperty() {
        return property;
    }

    public void setProperty(int property) {
        this.property = property;
    }

    @Override
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

    public boolean isAllAreaEmpty() {
        return Predicates.isEmpty(slow_speed_areas) && Predicates.isEmpty(middle_speed_areas)
                && Predicates.isEmpty(high_speed_areas);
    }

    public void setRawFile(String musicName) {
        filePath = musicName;
    }
    public String getRawFile(){
        return filePath;
    }

    public String getUniqueKey(){
        return getName() + ": " + getDuration();
    }

    public Float getMaxTime() {
        List<Float> times = getTimes();
        return times.get(times.size() - 1);
    }

    public String getCategoryStr() {
        return categoryStr;
    }

    public void setCategoryStr(String categoryStr) {
        this.categoryStr = categoryStr;
        Integer category = Configs.parseCategory(categoryStr);
        if(category == null){
            throw new RuntimeException("categoryStr = " + categoryStr);
        }
        this.category = category;
    }

    public int getCategory() {
        return category;
    }
    public void setCategory(int category) {
        this.category = category;
    }
}
