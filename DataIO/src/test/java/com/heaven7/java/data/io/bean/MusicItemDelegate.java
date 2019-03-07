package com.heaven7.java.data.io.bean;

import java.util.List;

/** @author heaven7 */
public interface MusicItemDelegate extends NameDeleagte{

    List<String> getDomains();

    int getProperty();

    int getRhythm();

    List<List<Float>> getSlow_speed_areas();

    List<List<Float>> getMiddle_speed_areas();

    List<List<Float>> getHigh_speed_areas();

    int getLineNumber();

    int getDuration();

    String getName();

    Float getMaxTime();
}
