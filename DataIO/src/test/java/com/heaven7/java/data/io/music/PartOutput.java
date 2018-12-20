package com.heaven7.java.data.io.music;

import com.heaven7.java.data.io.bean.MusicItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据不同领域不同属性输出.
 * @author heaven7
 */
public abstract class PartOutput {

    public abstract String getPartDomain();

    public abstract int getPartProperty();

    public abstract int getPartRhythm(); //节奏

    public abstract int getDuration();   //时长

    public String getFormatFilename(){
        return getPartDomain() + "_music_" + getPartRhythm() + "_" + getDuration() + "s";
    }

    public final List<MusicItem> collectDomainWithRhythm(List<MusicItem> rawItems){
        List<MusicItem> matchItems = new ArrayList<>();
        String domain = getPartDomain();
        int rhythm = getPartRhythm();
        int duration = getDuration();
        for(MusicItem item : rawItems){
            if(item.getDomains().contains(domain) && item.getRhythm() == rhythm
                    && item.getDuration() == duration){
                matchItems.add(item);
            }
        }
        return matchItems;
    }

    public final List<MusicItem> collectDomain(List<MusicItem> rawItems) {
        List<MusicItem> matchItems = new ArrayList<>();
        String domain = getPartDomain();
        for (MusicItem item : rawItems){
            if(item.getDomains().contains(domain)){
                matchItems.add(item);
            }
        }
        return matchItems;
    }

    public final List<MusicItem> collectPropertyRhythm(List<MusicItem> rawItems) {
        List<MusicItem> matchItems = new ArrayList<>();
        int property = getPartProperty();
        int rhythm = getPartRhythm();
        for (MusicItem item : rawItems){
            if(item.getProperty() == property && item.getRhythm() == rhythm){
                matchItems.add(item);
            }
        }
        return matchItems;
    }

    public static class SimplePartOutput extends PartOutput{
         private String domain;
         private int property;
         private int rhythm;
         private int duration;

        public SimplePartOutput(String domain, int property, int rhythm, int duration) {
            this.domain = domain;
            this.property = property;
            this.rhythm = rhythm;
            this.duration = duration;
        }
        @Override
        public String getPartDomain() {
            return domain;
        }
        @Override
        public int getPartProperty() {
            return property;
        }
        @Override
        public int getPartRhythm() {
            return rhythm;
        }
        @Override
        public int getDuration() {
            return duration;
        }
    }
}

