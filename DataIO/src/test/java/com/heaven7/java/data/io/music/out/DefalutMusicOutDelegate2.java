package com.heaven7.java.data.io.music.out;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
public class DefalutMusicOutDelegate2 implements MusicOutDelegate<MusicItem2> {

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
                FileUtils.writeTo(effectFile, mGson.toJson(mi.getSpecialEffectItem()));
                FileUtils.writeTo(transitionFile, mGson.toJson(mi.getTransitionItem()));
                FileUtils.writeTo(filterFile, mGson.toJson(mi.getFilterNames()));
                return null;
            }
        });
    }
    @Override
    public void copyValidMusics(String outDir, List<MusicItem2> items) {

    }


}
