package com.heaven7.java.data.io.music.in;


import com.heaven7.java.data.io.bean.MusicItemDelegate;

import java.util.List;

/**
 * @author heaven7
 */
public interface SpeedAreaSource {

    /**
     * get the speed area
     * @param areaType see {@linkplain com.heaven7.java.data.io.bean.CutConfigBeanV10#AREA_TYPE_LOW} and etc.
     * @param delegate the music item delegate
     * @return get the speed area
     */
    List<List<Float>> getSpeedArea(MusicItemDelegate delegate, int areaType);

}
