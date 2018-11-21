package com.heaven7.java.data.io.music;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heaven7.java.data.io.bean.MusicItem;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelDataServiceAdapter;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.heaven7.java.data.io.music.Configs.*;

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

    private final Gson mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private final String simpleFileName;
    private final String outDir;

    private final MusicCutProvider cutProvider;
    private final HashMap<String, List<Float>> cutMap = new HashMap<>();

    /**
     * create excel to json adapter
     * <p>this just used for test.</p>
     * @param simpleFileName the out json file name
     * @param cuts the cuts
     */
    @Deprecated
    public ExcelToJsonAdapter(String outDir, String simpleFileName,final String cuts) {
       this(outDir, simpleFileName, new MusicCutProvider() {
           @Override
           public String getCuts(String rowName) {
               return cuts;
           }
       });
    }
    /**
     * create excel to json adapter
     * <p>this just used for test.</p>
     * @param outDir
     * @param simpleFileName the out json file name
     * @param provider the cuts provider
     */
    public ExcelToJsonAdapter(String outDir, String simpleFileName, MusicCutProvider provider) {
        this.outDir = outDir;
        this.simpleFileName = simpleFileName;
        this.cutProvider = provider;
    }

    public List<Float> getCuts(String rowName){
        List<Float> cuts = cutMap.get(rowName);
        if(cuts == null){
            String cutStr = cutProvider.getCuts(rowName);
            if(cutStr == null){
                return null;
            }
            cuts = VisitServices.from(Arrays.asList(cutStr.split(",")))
                    .map(new ResultVisitor<String, Float>() {
                        @Override
                        public Float visit(String s, Object param) {
                            return Float.valueOf(s);
                        }
                    }).getAsList();
            cutMap.put(rowName, cuts);
        }
        return cuts;
    }

    @Override
    public int insertBatch(List<ExcelRow> t) {
        final StringBuilder sb_warn = new StringBuilder();
        final List<MusicItem> musicItems = VisitServices.from(t).map(new ResultVisitor<ExcelRow, MusicItem>() {
            @Override
            public MusicItem visit(ExcelRow row, Object param) {
                List<ExcelCol> columns = row.getColumns();
                String value = columns.get(INDEX_NAME).getColumnString();
                String value2 = columns.get(INDEX_DOMAIN).getColumnString();
                if (value == null || value.length() == 0) {
                    return null; //filter
                }
                if (value2 == null || value2.length() == 0) {
                    return null; //filter
                }


                MusicItem item = new MusicItem();
                item.setName(columns.get(INDEX_NAME).getColumnString());
                List<Float> cuts = getCuts(item.getName());
                //ignore
                if(cuts == null){
                    return null;
                }
                item.setTimes(cuts);
                item.setLineNumber(row.getRowIndex() + 1);
                item.setDomains(parseDomain(columns.get(INDEX_DOMAIN).getColumnString()));
                item.setProperty(parseMood(columns.get(INDEX_MOOD).getColumnString()));
                item.setRhythm(parseRhythm(columns.get(INDEX_RHYTHM).getColumnString()));
                item.setSlow_speed_areas(parseTimeAreas(item.getName(), columns.get(INDEX_SLOW_SPEED_AREAS).getColumnString()));
                item.setMiddle_speed_areas(parseTimeAreas(item.getName(),columns.get(INDEX_MIDDLE_SPEED_AREAS).getColumnString()));
                item.setHigh_speed_areas(parseTimeAreas(item.getName(),columns.get(INDEX_HIGH_SPEED_AREAS).getColumnString()));
                item.setId(item.getName());
                //check
                if(item.isAllAreaEmpty()){
                    return null;
                }
                //check
                ErrorVerifier.check(item, cuts.get(cuts.size()-1) , sb_warn);
                return item;
            }
        }).getAsList();
        //part outputs
        VisitServices.from(Configs.getAllParts()).fire(new FireVisitor<PartOutput>() {
            @Override
            public Boolean visit(PartOutput part, Object param) {
                List<String> list = VisitServices.from(part.collect(musicItems))
                        .map(new ResultVisitor<MusicItem, String>() {
                    @Override
                    public String visit(MusicItem item, Object param) {
                        return item.getName();
                    }
                }).getAsList();
                String partPath = outDir + File.separator + part.getPartDomain() + "_music_"
                        + part.getPartProperty()  + ".json";
                FileUtils.writeTo(partPath, mGson.toJson(list));
                return null;
            }
        });

        //total
        String json = mGson.toJson(musicItems);
        String outJsonFile = outDir + File.separator + simpleFileName + ".json";
        FileUtils.writeTo(outJsonFile, json);
        //warn
        String warnPath = outDir + File.separator + simpleFileName + "_warn.txt";
        FileUtils.writeTo(warnPath, sb_warn.toString());
        //one music-> one json
        VisitServices.from(musicItems).fire(new FireVisitor<MusicItem>() {
            @Override
            public Boolean visit(MusicItem item, Object param) {
                String path = outDir + File.separator + item.getName() + ".json";
                FileUtils.writeTo(path,  mGson.toJson(item));
                return null;
            }
        });

        return t.size();
    }

    private List<List<Float>> parseTimeAreas(final String rowName, String str) {
        if(str == null || str.length() == 0){
            return null;
        }
        String[] strs = str.split(";");
        return VisitServices.from(Arrays.asList(strs)).map(new ResultVisitor<String, List<Float>>() {
            @Override
            public List<Float> visit(String s, Object param) {
                return parseTimeArea(rowName, s).asList();
            }
        }).getAsList();
    }

    private float findNearest(String rowName, float src) {
        float delta = Float.MAX_VALUE;
        float nearest = src;
        for(Float val :  getCuts(rowName)){
            float del = Math.abs(val - src);
            if(del < delta){
                delta = del;
                nearest = val;
            }
        }
        return nearest;
    }
    private TimeArea parseTimeArea(String rowName, String str){
        if(!str.contains("-")){
            throw new IllegalStateException(str);
        }
        String[] ss = str.split("-");
        float start = parseFloatTime(ss[0]);
        float end = parseFloatTime(ss[1]);
        TimeArea ta = new TimeArea();
        ta.setBegin(findNearest(rowName, start));
        ta.setEnd(findNearest(rowName, end));
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

}
