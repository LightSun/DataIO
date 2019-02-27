package com.heaven7.java.data.io.music.in;

import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.data.io.bean.CutInfo;
import com.heaven7.java.data.io.bean.MusicItem2;

import java.util.Arrays;
import java.util.List;

/**
 * @author heaven7
 */
public class MultiMusicCutSource implements MusicCutSource {

    private final List<MusicCutSource> sources;

    public MultiMusicCutSource(MusicCutSource... sources){
        this(Arrays.asList(sources));
    }
    public MultiMusicCutSource(List<MusicCutSource> sources) {
        this.sources = sources;
    }

    @Override
    public List<CutInfo> getCutInfos(MusicItem2 mi) {
        List<CutInfo> result = null;
        for (MusicCutSource source : sources){
            result = source.getCutInfos(mi);
            if(!Predicates.isEmpty(result)){
                break;
            }
        }
        return result;
    }
}
