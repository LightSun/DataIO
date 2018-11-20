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

import java.io.File;
import java.util.ArrayList;
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
        sDomain_map.put("人物", "person");
        sDomain_map.put("聚会", "party");

        sMood_map.put("标准的", 1);
        sMood_map.put("多变的", 2);
        sMood_map.put("单调的", 0);

        sRhythm_map.put("快节奏", 2);
        sRhythm_map.put("中节奏", 1);
        sRhythm_map.put("慢节奏", 0);
    }
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
        List<MusicItem> musicItems = VisitServices.from(t).map(new ResultVisitor<ExcelRow, MusicItem>() {
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
                item.setLineNumber(row.getRowIndex() + 1);
                item.setDomains(parseDomain(columns.get(INDEX_DOMAIN).getColumnString()));
                item.setMatter(parseMood(columns.get(INDEX_MOOD).getColumnString()));
                item.setMatter(parseRhythm(columns.get(INDEX_RHYTHM).getColumnString()));
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
        //按照领域输出不同文件
        List<MusicItem> travels = new ArrayList<>();
        List<MusicItem> sports = new ArrayList<>();
        List<MusicItem> parties = new ArrayList<>();
        List<MusicItem> persons = new ArrayList<>();
        for(MusicItem mi :musicItems){
            if(mi.getDomains().contains("sport")){
                sports.add(mi);
            }
            if(mi.getDomains().contains("travel")){
                travels.add(mi);
            }
            if(mi.getDomains().contains("party")){
                parties.add(mi);
            }
            if(mi.getDomains().contains("person")){
                persons.add(mi);
            }
        }
        String travelPath = outDir + File.separator + simpleFileName + "_travel.json";
        String sportPath = outDir + File.separator + simpleFileName + "_sport.json";
        String personPath = outDir + File.separator + simpleFileName + "_person.json";
        String partyPath = outDir + File.separator + simpleFileName + "_party.json";
        FileUtils.writeTo(travelPath, mGson.toJson(travels));
        FileUtils.writeTo(sportPath, mGson.toJson(sports));
        FileUtils.writeTo(personPath, mGson.toJson(persons));
        FileUtils.writeTo(partyPath, mGson.toJson(parties));
        //total
        String json = mGson.toJson(musicItems);
        String outJsonFile = outDir + File.separator + simpleFileName + ".json";
        FileUtils.writeTo(outJsonFile, json);
        //warn
        String warnPath = outDir + File.separator + simpleFileName + "_warn.txt";
        FileUtils.writeTo(warnPath, sb_warn.toString());
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
