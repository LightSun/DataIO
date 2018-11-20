package com.heaven7.java.data.io.music;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heaven7.java.data.io.bean.MusicItem;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelDataServiceAdapter;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author heaven7
 */
public class ExcelToJsonAdapter extends ExcelDataServiceAdapter {

    public static final int INDEX_NAME   = 0;
    public static final int INDEX_DOMAIN = 1;
    public static final int INDEX_MOOD = 2;
    public static final int INDEX_RHYTHM = 3;
    public static final int INDEX_SLOW_SPEED_AREAS = 4;
    public static final int INDEX_MIDDLE_SPEED_AREAS = 5;
    public static final int INDEX_HIGH_SPEED_AREAS = 6;

    private static final HashMap<String, String> sDomain_map = new HashMap<>();
    private static final HashMap<String, Integer> sMood_map = new HashMap<>();
    private static final HashMap<String, Integer> sRhythm_map = new HashMap<>();

    static {
        sDomain_map.put("运动", "sport");
        sDomain_map.put("旅行", "travel");

        sMood_map.put("标准的", 1);
        sMood_map.put("多变的", 2);
        sMood_map.put("单调的", 0);

        sRhythm_map.put("快节奏", 2);
        sRhythm_map.put("中节奏", 1);
        sRhythm_map.put("慢节奏", 0);
    }
    private final Gson mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private final String outJsonFilename;
    private final List<Float> cuts;
    private String warnFile;

    public ExcelToJsonAdapter(String outJsonFilename, MusicCutProvider musicCutProvider) {
       this(outJsonFilename, musicCutProvider.getCuts());
    }
    public ExcelToJsonAdapter(String outJsonFilename, String cuts) {
        this.outJsonFilename = outJsonFilename;
        this.cuts = VisitServices.from(Arrays.asList(cuts.split(",")))
                .map(new ResultVisitor<String, Float>() {
                    @Override
                    public Float visit(String s, Object param) {
                        return Float.parseFloat(s);
                    }
                }).getAsList();
    }

    public void setWarnFile(String warnFile) {
        this.warnFile = warnFile;
    }
    @Override
    public int insertBatch(List<ExcelRow> t) {
        final StringBuilder sb_warn = new StringBuilder();
        List<MusicItem> musicItems = VisitServices.from(t).map(new ResultVisitor<ExcelRow, MusicItem>() {
            @Override
            public MusicItem visit(ExcelRow row, Object param) {
                List<ExcelCol> columns = row.getColumns();
                String value = columns.get(0).getColumnString();
                String value2 = columns.get(1).getColumnString();
                if (value == null || value.length() == 0) {
                    return null; //filter
                }
                if (value2 == null || value2.length() == 0) {
                    return null; //filter
                }
                MusicItem item = new MusicItem();
                item.setLineNumber(row.getRowIndex() + 1);
                item.setName(columns.get(INDEX_NAME).getColumnString());
                item.setDomains(parseDomain(columns.get(INDEX_DOMAIN).getColumnString()));
                item.setMatter(parseMood(columns.get(INDEX_MOOD).getColumnString()));
                item.setMatter(parseRhythm(columns.get(INDEX_RHYTHM).getColumnString()));
                item.setSlow_speed_areas(parseTimeAreas(columns.get(INDEX_SLOW_SPEED_AREAS).getColumnString()));
                item.setMiddle_speed_areas(parseTimeAreas(columns.get(INDEX_MIDDLE_SPEED_AREAS).getColumnString()));
                item.setHigh_speed_areas(parseTimeAreas(columns.get(INDEX_HIGH_SPEED_AREAS).getColumnString()));
                //check
                ErrorVerifier.check(item, cuts.get(cuts.size()-1) , sb_warn);
                return item;
            }
        }).getAsList();
        String json = mGson.toJson(musicItems);
        FileUtils.writeTo(outJsonFilename, json);
        if(warnFile != null){
            FileUtils.writeTo(warnFile, sb_warn.toString());
        }
        return t.size();
    }

    private List<List<Float>> parseTimeAreas(String str) {
        if(str == null || str.length() == 0){
            return null;
        }
        String[] strs = str.split(";");
        return VisitServices.from(Arrays.asList(strs)).map(new ResultVisitor<String, List<Float>>() {
            @Override
            public List<Float> visit(String s, Object param) {
                return parseTimeArea(s).asList();
            }
        }).getAsList();
    }

    private float findNearest(float src) {
        float delta = Float.MAX_VALUE;
        float nearest = src;
        for(Float val : cuts){
            float del = Math.abs(val - src);
            if(del < delta){
                delta = del;
                nearest = val;
            }
        }
        return nearest;
    }
    private TimeArea parseTimeArea(String str){
        if(!str.contains("-")){
            throw new IllegalStateException(str);
        }
        String[] ss = str.split("-");
        float start = parseFloatTime(ss[0]);
        float end = parseFloatTime(ss[1]);
        TimeArea ta = new TimeArea();
        ta.setBegin(findNearest(start));
        ta.setEnd(findNearest(end));
        return ta;
    }

    private static float parseFloatTime(String s) {
        if(s.contains(":")){
            String[] ss = s.split(":");
            int minute = parseDigital(ss[0]);
            float second = parseSecond(ss[1]);
            return minute * 60 + second;
        }else{
            return parseSecond(s);
        }
    }

    private static float parseSecond(String s) {
        while (s.startsWith("0")){
            s = s.substring(1);
        }
        if(s.length() == 0){
            return 0;
        }
        try{
            return Float.parseFloat(s);
        }catch (NumberFormatException e){
            throw new RuntimeException(s, e);
        }
    }

    private static int parseDigital(String s) {
        while (s.startsWith("0")){
            s = s.substring(1);
        }
        if(s.length() == 0){
            return 0;
        }
        return Integer.parseInt(s);
    }

    private static int parseRhythm(String str) {
        return sRhythm_map.get(str);
    }

    private static List<String> parseDomain(String str) {
        String[] strs = str.split(";");
        return VisitServices.from(Arrays.asList(strs)).map(new ResultVisitor<String, String>() {
            @Override
            public String visit(String s, Object param) {
                return sDomain_map.get(s);
            }
        }).getAsList();
    }

    private static int parseMood(String str) {
        return sMood_map.get(str);
    }
}
