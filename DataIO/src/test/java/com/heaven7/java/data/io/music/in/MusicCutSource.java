package com.heaven7.java.data.io.music.in;

import com.heaven7.java.data.io.bean.CutInfo;
import com.heaven7.java.data.io.bean.MusicItem2;

import java.util.List;

/**
 * @author heaven7
 */
public interface MusicCutSource {

    List<CutInfo> getCutInfos(MusicItem2 mi);

}
