package com.heaven7.java.data.io.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.data.io.bean.jsonAdapter.MusicItem2JsonAdapter;
import com.heaven7.java.data.io.music.Configs;
import com.heaven7.java.visitor.MapFireVisitor;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static com.heaven7.java.data.io.utils.MusicItem2Helper.getEffectOutItem;

/**
 * @author heaven7
 */
@JsonAdapter(MusicItem2JsonAdapter.class)
public class MusicItem2 implements MusicItemDelegate{

    @Expose
    private String id = "default";
    @Expose(serialize = false)
    private String name;
    @Expose
    private List<String> domains;

    @Expose
    private int duration; //in seconds

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
    private int category; //领域

    //===========================================
    private List<EffectInfo> effectInfos;
    private List<EffectInfo> transitionInfos;
    private List<String> filterNames;
    @Expose
    private List<CutInfo> cutInfos;
    @Expose
    @SerializedName("transition_cuts")
    private List<Float> transitionCuts;

    public EffectOutItem getSpecialEffectItem(){
        return getEffectOutItem(effectInfos);
    }

    public EffectOutItem getTransitionItem(){
        return getEffectOutItem(transitionInfos);
    }
    //----------------------------------------------

    public List<EffectInfo> getTransitionInfos() {
        return transitionInfos;
    }
    public void setTransitionInfos(List<EffectInfo> transitionInfos) {
        this.transitionInfos = transitionInfos;
    }

    public List<Float> getTransitionCuts() {
        return transitionCuts;
    }
    public void setTransitionCuts(List<Float> transitionCuts) {
        this.transitionCuts = transitionCuts;
    }

    public List<String> getFilterNames() {
        return filterNames;
    }
    public void setFilterNames(List<String> filterNames) {
        this.filterNames = filterNames;
    }

    public String getFilterName() {
        return Predicates.isEmpty(filterNames) ? null : filterNames.get(0);
    }

    public List<EffectInfo> getEffectInfos() {
        return effectInfos;
    }
    public void setEffectInfos(List<EffectInfo> effectInfos) {
        this.effectInfos = effectInfos;
    }

    public List<CutInfo> getCutInfos() {
        return cutInfos;
    }
    public void setCutInfos(List<CutInfo> cutInfos) {
        this.cutInfos = cutInfos;
    }

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


    public List<String> getDomains() {
        return domains;
    }
    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public int getProperty() {
        return property;
    }

    public void setProperty(int property) {
        this.property = property;
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

    @Override
    public Float getMaxTime() {
        if(cutInfos == null){
            return null;
        }
        CutInfo info = VisitServices.from(cutInfos).query(new PredicateVisitor<CutInfo>() {
            @Override
            public Boolean visit(CutInfo cutInfo, Object param) {
                return cutInfo.getType() == CutInfo.TYPE_INTENSIVE;
            }
        });
        return info != null ? info.getMaxTime() : null;
    }

    public String genUniqueId() {
        return getId() + "_" + duration;
    }

    public void addTransitionInfos(List<EffectInfo> infos) {
        if(transitionInfos == null){
            transitionInfos = new ArrayList<>();
        }
        transitionInfos.addAll(infos);
    }

    public void addEffectInfos(List<EffectInfo> infos) {
        if(effectInfos == null){
            effectInfos = new ArrayList<>();
        }
        effectInfos.addAll(infos);
    }
}
