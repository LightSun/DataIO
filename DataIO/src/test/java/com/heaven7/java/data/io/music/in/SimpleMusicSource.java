package com.heaven7.java.data.io.music.in;

import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.bean.MusicMappingItem;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * if dir is e:/ the music source , 60s should be : e:/60s
 * @author heaven7
 */
public class SimpleMusicSource implements MusicSource {

    private final String dir;
    private final HashMap<Integer, List<MusicMappingItem>> cache = new HashMap<>();

    public SimpleMusicSource(String dir) {
        this.dir = dir;
    }

    @Override
    public String getMusicPath(final MusicItem2 mi) {
        String subDir = dir + File.separator + mi.getDuration();
        if(!new File(subDir).exists()){
            subDir += "s";
        }
        if(!new File(subDir).exists()){
            return null;
        }
        List<MusicMappingItem> items = cache.get(mi.getDuration());
        if(items == null){
            List<String> files = FileUtils.getFiles(new File(subDir), "mp3");
            items = VisitServices.from(files).map(new ResultVisitor<String, MusicMappingItem>() {
                @Override
                public MusicMappingItem visit(String s, Object param) {
                    MusicMappingItem mmi = new MusicMappingItem();
                    mmi.setFilename(s);
                    mmi.setMusicName(UniformNameHelper.uniformMusicFilename(s));
                    return mmi;
                }
            }).getAsList();
            cache.put(mi.getDuration(), items);
        }
        MusicMappingItem result = VisitServices.from(items).query(new PredicateVisitor<MusicMappingItem>() {
            @Override
            public Boolean visit(MusicMappingItem mmi, Object param) {
                return mmi.getMusicName().equals(mi.getName());
            }
        });
        return result != null ? result.getFilename() : null;
    }
}
