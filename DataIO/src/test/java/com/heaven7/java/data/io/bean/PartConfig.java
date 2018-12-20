package com.heaven7.java.data.io.bean;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.heaven7.java.data.io.utils.MapGsonAdapter;

import java.io.IOException;
import java.util.*;

/**
 * @author heaven7
 */
@JsonAdapter(PartConfig.PartConfigAdapter.class)
public class PartConfig {

    @JsonAdapter(PartsAdapter.class)
    private Map<String, List<PartItem>> parts;

    public Map<String, List<PartItem>> getParts() {
        return parts;
    }

    public void setParts(Map<String, List<PartItem>> parts) {
        this.parts = parts;
    }

    public static class PartConfigAdapter extends TypeAdapter<PartConfig>{

        private final PartsAdapter mAdapter = new PartsAdapter();
        @Override
        public void write(JsonWriter out, PartConfig value) throws IOException {
            if(value.getParts() != null) {
                mAdapter.write(out, value.getParts());
            }
        }
        @Override
        public PartConfig read(JsonReader in) throws IOException {
            Map<String, List<PartItem>> map = mAdapter.read(in);
            PartConfig config = new PartConfig();
            config.setParts(map);
            return config;
        }
    }

    public static class PartsAdapter extends MapGsonAdapter<List<PartItem>> {

        private final PartItem.IdsAdapter idsAdapter = new PartItem.IdsAdapter();

        @Override
        protected List<PartItem> readValue(JsonReader in) throws IOException {
            List<PartItem> list = new ArrayList<>();
            in.beginArray();
            while (in.hasNext()) {
                in.beginObject();
                PartItem item = new PartItem();
                while (in.hasNext()) {
                    switch (in.nextName()) {
                        case PartItem.PROP_IDS: {
                            item.setIdMap(idsAdapter.read(in));
                        }
                        break;
                        case PartItem.PROP_PROPERTY:
                            item.setProperty(in.nextInt());
                            break;
                        case PartItem.PROP_RHYTHM:
                            item.setRhythm(in.nextInt());
                            break;
                    }
                }
                in.endObject();
                list.add(item);
            }
            in.endArray();
            return list;
        }

        @Override
        protected void writeValue(JsonWriter out, List<PartItem> value) throws IOException {
            out.beginArray();
            for (PartItem item : value) {
                out.beginObject();
                out.name(PartItem.PROP_RHYTHM).value(item.getRhythm());
                out.name(PartItem.PROP_PROPERTY).value(item.getProperty());
                out.name(PartItem.PROP_IDS);
                if (item.getIdMap() != null) {
                    idsAdapter.write(out, item.getIdMap());
                }
                out.endObject();
            }
            out.endArray();
        }
    }

    public static void main(String[] args) {
        Map<String, List<PartItem>> map = new HashMap<>();
        map.put("travel", createItems());
        map.put("sport", createItems());

        PartConfig config = new PartConfig();
        config.setParts(map);
        System.out.println(new Gson().toJson(config));


        //直接序列化map. 注册外层的map TypeAdapter是没用的
       /* System.out.println(new GsonBuilder()
                .registerTypeAdapter(Map.class, new PartsAdapter())
                .create().toJson(map));*/
    }

    private static List<PartItem> createItems() {
        List<PartItem> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            PartItem item = new PartItem();
            Map<String, List<String>> idsMap = new HashMap<>();
            idsMap.put("60", Arrays.asList("id1", "id2"));
            idsMap.put("10", Arrays.asList("id1", "id2"));
            item.setIdMap(idsMap);
            item.setRhythm(i % 2);
            item.setProperty(i % 5);
            list.add(item);
        }
        return list;
    }
}
