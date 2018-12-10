package com.heaven7.java.data.io.music.provider;

import com.heaven7.java.data.io.bean.MusicItem;

/**
 * @author heaven7
 */
public interface SpeedMusicCutProvider extends MusicCutProvider {

     boolean fillSpeedAreasForMusicItem(String rowName, MusicItem item);

}
