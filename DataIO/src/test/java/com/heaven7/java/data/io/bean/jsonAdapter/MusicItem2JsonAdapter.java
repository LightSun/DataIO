package com.heaven7.java.data.io.bean.jsonAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.data.io.bean.CutInfo;
import com.heaven7.java.data.io.bean.MusicItem2;

import java.io.IOException;
import java.util.List;

/**
 * @author heaven7
 */
public class MusicItem2JsonAdapter extends TypeAdapter<MusicItem2> {

    @Override
    public void write(JsonWriter out, MusicItem2 value) throws IOException {
        //id, property, rhythm, domains
        out.beginObject();
        out.name("id").value(value.getMusicId() + "_" + value.getDuration());
        out.name("name").value(value.getDisplayName());
        out.name("singer").value(value.getSinger());

        out.name("property").value(value.getProperty());
        out.name("rhythm").value(value.getRhythm());
        out.name("domains");
        out.beginArray();
        for (String domain : value.getDomains()){
            out.value(domain);
        }
        out.endArray();
        // dense_cuts and sparse_cuts
        assert value.getCutInfos().size() <= 2;
        for(CutInfo info : value.getCutInfos()){
            String name = info.getType() == CutInfo.TYPE_INTENSIVE ? "dense_times" : "sparse_times";
            out.name(name);
            out.beginArray();
            for (Float val : info.getCuts()){
                out.value(val);
            }
            out.endArray();
        }
        //transition_cuts
        if(!Predicates.isEmpty(value.getTransitionCuts())){
            out.name("transition_times");
            out.beginArray();
            for (Float val : value.getTransitionCuts()){
                out.value(val);
            }
            out.endArray();
        }
        //slow/middle/high  speed_areas
        writeAreas(out, "slow_speed_areas", value.getSlow_speed_areas());
        writeAreas(out, "middle_speed_areas", value.getMiddle_speed_areas());
        writeAreas(out, "high_speed_areas", value.getHigh_speed_areas());

        out.endObject();
    }

    private void writeAreas(JsonWriter out, String name, List<List<Float>> list)throws IOException {
        out.name(name);
        out.beginArray();
        if(!Predicates.isEmpty(list)){
            for (List<Float> list1 : list){
                out.beginArray();
                for (Float val : list1){
                    out.value(val);
                }
                out.endArray();
            }
        }
        out.endArray();
    }

    @Override
    public MusicItem2 read(JsonReader in) throws IOException {
        return null;
    }
}
