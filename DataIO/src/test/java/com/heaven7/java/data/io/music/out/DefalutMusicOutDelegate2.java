package com.heaven7.java.data.io.music.out;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.data.io.bean.*;
import com.heaven7.java.data.io.bean.jsonAdapter.MusicItem2JsonAdapter;
import com.heaven7.java.data.io.music.Configs;
import com.heaven7.java.data.io.music.PartOutput;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.MapFireVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author heaven7
 */
public class DefalutMusicOutDelegate2 implements MusicOutDelegate2 {

    private final Gson mGson = new GsonBuilder().registerTypeAdapter(MusicItem2.class, new MusicItem2JsonAdapter()).create();

    @Override
    public void writePart(final String outDir, final List<MusicItem2> items) {
    }

    @Override
    public void writeTotal(String outDir, String simpleFileName, List<MusicItem2> items) {
    }

    @Override
    public void writeWarn(String outDir, String simpleFileName, String warnMessages) {
    }

    @Override
    public void writeItem(final String outDir, List<MusicItem2> items) {
        VisitServices.from(items).fire(new FireVisitor<MusicItem2>() {
            @Override
            public Boolean visit(MusicItem2 mi, Object param) {
                String infoFile = outDir + File.separator + "music_" + mi.genUniqueId() + ".json";
                String effectFile = outDir + File.separator + "effect_" + mi.genUniqueId() + ".json";
                String transitionFile = outDir + File.separator + "transition_" + mi.genUniqueId() + ".json";
                String filterFile = outDir + File.separator + "filter_" + mi.genUniqueId() + ".json";
                FileUtils.writeTo(infoFile, mGson.toJson(mi));
                EffectOutItem item = mi.getSpecialEffectItem();
                if(item != null){
                    FileUtils.writeTo(effectFile, mGson.toJson(item));
                }

                item = mi.getTransitionItem();
                if(item != null){
                    FileUtils.writeTo(transitionFile, mGson.toJson(item));
                }
                if(!Predicates.isEmpty(mi.getFilterNames())){
                    FileUtils.writeTo(filterFile, mGson.toJson(mi.getFilterNames()));
                }
                return null;
            }
        });
    }
    @Override
    public void copyValidMusics(String outDir, List<MusicItem2> items) {
        final File out = new File(outDir, "musics");
        FileUtils.deleteDir(out);
        out.mkdirs();
        List<MusicMappingItem> maps = VisitServices.from(items).map(new ResultVisitor<MusicItem2, MusicMappingItem>() {
            @Override
            public MusicMappingItem visit(MusicItem2 item, Object param) {
                Float maxTime = item.getMaxTime();
                File dst = new File(out, item.getId() + "." + FileUtils.getFileExtension(item.getRawFile()));
                MusicMappingItem mmi = new MusicMappingItem();
                mmi.setMusicName(item.getName());
                mmi.setId(item.getId());
                mmi.setFullId(dst.getAbsolutePath());
                mmi.setFilename(item.getRawFile());
                mmi.setDuration(maxTime);
                return mmi;
            }
        }).fire(new FireVisitor<MusicMappingItem>() {
            @Override
            public Boolean visit(MusicMappingItem mmi, Object param) {
                FileUtils.copyFile(new File(mmi.getFilename()), new File(mmi.getFullId()));
                return null;
            }
        }).getAsList();
        //save mapping
        final File file_mapping = new File(outDir, "name_id_mapping.txt");
        FileUtils.writeTo(file_mapping, mGson.toJson(maps));
    }


}
