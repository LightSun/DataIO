package com.heaven7.java.data.io.music;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heaven7.java.data.io.bean.MusicItem;
import com.heaven7.java.data.io.bean.MusicMappingItem;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelDataServiceAdapter;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.data.io.utils.FileMd5Helper;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.*;

import static com.heaven7.java.data.io.music.Configs.*;

/**
 * @author heaven7
 */
public class ExcelToJsonAdapter extends ExcelDataServiceAdapter {

    public static final int INDEX_NAME = 0;
    public static final int INDEX_DOMAIN = 1;
    public static final int INDEX_MOOD = 2;
    public static final int INDEX_RHYTHM = 3;
    public static final int INDEX_SLOW_SPEED_AREAS = 4;
    public static final int INDEX_MIDDLE_SPEED_AREAS = 5;
    public static final int INDEX_HIGH_SPEED_AREAS = 6;

    private final Gson mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private final String simpleFileName;
    private final String outDir;
    private String musicInputDir;

    private final MusicCutProvider cutProvider;
    private final HashMap<String, List<Float>> cutMap = new HashMap<>();

    /**
     * create excel to json adapter
     * <p>this just used for test.</p>
     *
     * @param simpleFileName the out json file name
     * @param cuts           the cuts
     */
    @Deprecated
    public ExcelToJsonAdapter(String outDir, String simpleFileName, final String cuts) {
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
     *
     * @param outDir
     * @param simpleFileName the out json file name
     * @param provider       the cuts provider
     */
    public ExcelToJsonAdapter(String outDir, String simpleFileName, MusicCutProvider provider) {
        this.outDir = outDir;
        this.simpleFileName = simpleFileName;
        this.cutProvider = provider;
    }

    public List<Float> getCuts(String rowName) {
        List<Float> cuts = cutMap.get(rowName);
        if (cuts == null) {
            String cutStr = cutProvider.getCuts(rowName);
            if (cutStr == null) {
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
    public int insertBatch(final List<ExcelRow> t) {
        final StringBuilder sb_warn = new StringBuilder();
        final List<MusicItem> expectItems = VisitServices.from(t).map(new ResultVisitor<ExcelRow, MusicItem>() {
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
                if (cuts == null) {
                    return null;
                }
                List<TimeArea> areas = new ArrayList<>();

                item.setTimes(cuts);
                item.setLineNumber(row.getRowIndex() + 1);
                item.setDomains(parseDomain(columns.get(INDEX_DOMAIN).getColumnString()));
                item.setProperty(parseMood(columns.get(INDEX_MOOD).getColumnString()));
                item.setRhythm(parseRhythm(columns.get(INDEX_RHYTHM).getColumnString()));
                item.setSlow_speed_areas(parseTimeAreas(item.getName(), columns.get(INDEX_SLOW_SPEED_AREAS).getColumnString(), areas));
                item.setMiddle_speed_areas(parseTimeAreas(item.getName(), columns.get(INDEX_MIDDLE_SPEED_AREAS).getColumnString(), areas));
                item.setHigh_speed_areas(parseTimeAreas(item.getName(), columns.get(INDEX_HIGH_SPEED_AREAS).getColumnString(),areas));
                item.setId(item.getName());
                //check
                if (item.isAllAreaEmpty()) {
                    return null;
                }
                writeMappingFile(item, areas);
                //check
                ErrorVerifier.check(item, cuts.get(cuts.size() - 1), sb_warn);
                return item;
            }
        }).getAsList();
        // for map raw mp3 file
        final List<String> mp3s = FileUtils.getFiles(new File(musicInputDir), "mp3");
        final List<MusicItem> musicItems = VisitServices.from(expectItems).filter(new PredicateVisitor<MusicItem>() {
            @Override
            public Boolean visit(MusicItem item, Object param) {
                String name = item.getName();
                for(String mp3 : mp3s){
                    if(FileUtils.getFileName(mp3).equals(name)){
                        item.setId(FileMd5Helper.getMD5Three(mp3));
                        item.setRawFile(mp3);
                        return true;
                    }
                }
                System.out.println("can't find mp3 file for '"+ item.getName() +"'");
                return false;
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
                                return item.getId();
                            }
                        }).getAsList();
                if (list.isEmpty()) {
                    System.out.println("no items for '"+ part.getFormatFilename() + ".json'");
                    return false;
                }
                String partPath = outDir + File.separator + part.getFormatFilename() + ".json";
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
                String path = outDir + File.separator + item.getId() + ".json";
                FileUtils.writeTo(path, mGson.toJson(item));
                return null;
            }
        });
        //copy music to one dir
        if(musicInputDir != null){
            final File out = new File(outDir, "musics");
            FileUtils.deleteDir(out);
            out.mkdirs();
            List<MusicMappingItem> maps = VisitServices.from(musicItems).map(new ResultVisitor<MusicItem, MusicMappingItem>() {
                @Override
                public MusicMappingItem visit(MusicItem item, Object param) {
                    List<Float> times = item.getTimes();
                    Float val = times.get(times.size() - 1);
                    File dst = new File(out, item.getId() + "." + FileUtils.getFileExtension(item.getRawFile()));
                    MusicMappingItem mmi = new MusicMappingItem();
                    mmi.setMusicName(item.getName());
                    mmi.setId(item.getId());
                    mmi.setFullId(dst.getAbsolutePath());
                    mmi.setFilename(item.getRawFile());
                    mmi.setDuration(val);
                    return mmi;
                }
            }).fire(new FireVisitor<MusicMappingItem>() {
                @Override
                public Boolean visit(MusicMappingItem mmi, Object param) {
                    FileUtils.copyFile(new File(mmi.getFilename()), new File(mmi.getFullId()));
                    return null;
                }
            }).getAsList();
            final File file_mapping = new File(outDir, "name_id_mapping.txt");
            FileUtils.writeTo(file_mapping, mGson.toJson(maps));
        }
        return t.size();
    }

    public void setInputMusicDir(String musicDir) {
        this.musicInputDir = musicDir;
    }

    private void writeMappingFile(MusicItem item, List<TimeArea> areas) {
        //write mapping
        String outFile = outDir + File.separator + item.getId() + "___mapping.txt";
        StringBuilder sb_mapping = new StringBuilder();
        Collections.sort(areas);
        for (TimeArea ta : areas){
            sb_mapping.append(ta.toMappingText()).append("\r\n");
        }
        FileUtils.writeTo(outFile, sb_mapping.toString());
    }

    private List<List<Float>> parseTimeAreas(final String rowName, String str, final List<TimeArea> tas) {
        if (str == null || str.length() == 0) {
            return null;
        }
        String[] strs = str.split(";");
        return VisitServices.from(Arrays.asList(strs)).map(new ResultVisitor<String, List<Float>>() {
            @Override
            public List<Float> visit(String s, Object param) {
                TimeArea timeArea = parseTimeArea(rowName, s);
                tas.add(timeArea);
                return timeArea.asList();
            }
        }).getAsList();
    }

    private float findNearest(String rowName, float src) {
        float delta = Float.MAX_VALUE;
        float nearest = src;
        for (Float val : getCuts(rowName)) {
            float del = Math.abs(val - src);
            if (del < delta) {
                delta = del;
                nearest = val;
            }
        }
        return nearest;
    }

    private TimeArea parseTimeArea(String rowName, String str) {
        if (!str.contains("-")) {
            throw new IllegalStateException(str);
        }
        String[] ss = str.split("-");
        float start = parseFloatTime(ss[0]);
        float end = parseFloatTime(ss[1]);
        TimeArea ta = new TimeArea();
        ta.setBegin(findNearest(rowName, start));
        ta.setEnd(findNearest(rowName, end));
        ta.setRawBegin(ss[0]);
        ta.setRawEnd(ss[1]);
        return ta;
    }

    private static float parseFloatTime(String s) {
        if (s.contains(":")) {
            String[] ss = s.split(":");
            int minute = parseDigital(ss[0]);
            float second = parseSecond(ss[1]);
            return minute * 60 + second;
        } else {
            return parseSecond(s);
        }
    }

    private static float parseSecond(String s) {
        while (s.startsWith("0")) {
            s = s.substring(1);
        }
        if (s.length() == 0) {
            return 0;
        }
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            throw new RuntimeException(s, e);
        }
    }

    private static int parseDigital(String s) {
        while (s.startsWith("0")) {
            s = s.substring(1);
        }
        if (s.length() == 0) {
            return 0;
        }
        return Integer.parseInt(s);
    }
}
