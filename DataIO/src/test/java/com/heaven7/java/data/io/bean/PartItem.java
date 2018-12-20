package com.heaven7.java.data.io.bean;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.heaven7.java.data.io.utils.MapGsonAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author heaven7
 */
public class PartItem {

    public static final String PROP_PROPERTY  = "property";
    public static final String PROP_RHYTHM    = "rhythm";
    public static final String PROP_IDS       = "ids";

    private int property;
    private int rhythm;

    @SerializedName(PROP_IDS)
    @JsonAdapter(IdsAdapter.class)
    private Map<String, List<String>> idMap;

    public int getProperty() {
        return property;
    }
    public void setProperty(int property) {
        this.property = property;
    }

    public int getRhythm() {
        return rhythm;
    }
    public void setRhythm(int rhythm) {
        this.rhythm = rhythm;
    }

    public Map<String, List<String>> getIdMap() {
        return idMap;
    }
    public void setIdMap(Map<String, List<String>> idMap) {
        this.idMap = idMap;
    }

    public static class IdsAdapter extends MapGsonAdapter<List<String>> {

        @Override
        protected List<String> readValue(JsonReader in) throws IOException {
            List<String> list = new ArrayList<>();
            in.beginArray();
            while (in.hasNext()){
                list.add(in.nextString());
            }
            in.endArray();
            return list;
        }
        @Override
        protected void writeValue(JsonWriter out, List<String> value) throws IOException {
            out.beginArray();
            for (String val : value){
                out.value(val);
            }
            out.endArray();
        }
    }
}
