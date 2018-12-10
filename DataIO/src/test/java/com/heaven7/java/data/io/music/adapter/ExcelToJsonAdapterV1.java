package com.heaven7.java.data.io.music.adapter;

import com.heaven7.java.data.io.bean.MusicItem;
import com.heaven7.java.data.io.bean.TimeArea;
import com.heaven7.java.data.io.music.ErrorVerifier;
import com.heaven7.java.data.io.music.out.MusicOutDelegate;
import com.heaven7.java.data.io.music.provider.MusicCutProvider;
import com.heaven7.java.data.io.music.provider.SpeedMusicCutProvider;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelDataServiceAdapter;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.data.io.utils.FileMd5Helper;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.*;

import static com.heaven7.java.data.io.music.Configs.*;

/**
 * @author heaven7
 */
public class ExcelToJsonAdapterV1 extends ExcelDataServiceAdapter {

    private final String simpleFileName;
    private final String outDir;
    private String musicInputDir;

    private final HashMap<String, List<Float>> cutMap = new HashMap<>();
    private final MusicCutProvider cutProvider;
    private final IndexDelegate mIndexDelegate;
    private MusicOutDelegate mMusicOutDelegate;

    /**
     * create excel to json adapter
     * <p>this just used for test.</p>
     *
     * @param simpleFileName the out json file name
     * @param cuts           the cuts
     */
    @Deprecated
    public ExcelToJsonAdapterV1(String outDir, String simpleFileName, final String cuts) {
        this(outDir, simpleFileName, new MusicCutProvider.DefaultMusicCutProvider(cuts));
    }
    /**
     * create excel to json adapter
     * <p>this just used for test.</p>
     *
     * @param outDir
     * @param simpleFileName the out json file name
     * @param provider       the cuts provider
     */
    public ExcelToJsonAdapterV1(String outDir, String simpleFileName, MusicCutProvider provider) {
        this(outDir, simpleFileName, provider, new IndexDelegate());
    }
    /**
     * create excel to json adapter
     * <p>this just used for test.</p>
     *
     * @param outDir
     * @param simpleFileName the out json file name
     * @param provider       the cuts provider
     */
    protected ExcelToJsonAdapterV1(String outDir, String simpleFileName, MusicCutProvider provider, IndexDelegate indexDelegate) {
        super();
        this.outDir = outDir;
        this.simpleFileName = simpleFileName;
        this.cutProvider = provider;
        this.mIndexDelegate = indexDelegate;
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
                if(filter(row)){
                    return null;
                }
                List<ExcelCol> columns = row.getColumns();

                MusicItem item = new MusicItem();
                item.setName(columns.get(mIndexDelegate.getNameIndex()).getColumnString());
                List<Float> cuts = getCuts(item.getName());
                //ignore
                if (cuts == null) {
                    return null;
                }
                List<TimeArea> areas = new ArrayList<>();

                item.setTimes(cuts);
                item.setLineNumber(row.getRowIndex() + 1);
                item.setDomains(parseDomain(columns.get(mIndexDelegate.getDomainIndex()).getColumnString()));
                item.setProperty(parseMood(columns.get(mIndexDelegate.getMoodIndex()).getColumnString()));
                item.setRhythm(parseRhythm(columns.get(mIndexDelegate.getRhythmIndex()).getColumnString()));
                item.setId(item.getName());

                if(!(cutProvider instanceof SpeedMusicCutProvider) ||
                        !((SpeedMusicCutProvider) cutProvider).fillSpeedAreasForMusicItem(item.getName(), item)){
                    item.setSlow_speed_areas(parseTimeAreas(item.getName(), columns.get(mIndexDelegate.getSlowSpeedIndex()).getColumnString(), areas));
                    item.setMiddle_speed_areas(parseTimeAreas(item.getName(), columns.get(mIndexDelegate.getMiddleSpeedIndex()).getColumnString(), areas));
                    item.setHigh_speed_areas(parseTimeAreas(item.getName(), columns.get(mIndexDelegate.getHighSpeedIndex()).getColumnString(),areas));
                    //old need mapping
                    writeMappingFile(item, areas);
                    //old need check
                    ErrorVerifier.check(item, cuts.get(cuts.size() - 1), sb_warn);
                }
                //check
                if (item.isAllAreaEmpty()) {
                    return null;
                }
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
        mMusicOutDelegate.writePart(outDir, musicItems);
        //total
        mMusicOutDelegate.writeTotal(outDir, simpleFileName, musicItems);
        //warn
        mMusicOutDelegate.writeWarn(outDir, simpleFileName, sb_warn.toString());
        //one music-> one json
        mMusicOutDelegate.writeItem(outDir, musicItems);
        //copy music to one dir
        if(musicInputDir != null){
            mMusicOutDelegate.copyValidMusics(outDir, musicItems);
        }
        return t.size();
    }

    public void setMusicOutDelegate(MusicOutDelegate delegate) {
        this.mMusicOutDelegate = delegate;
    }

    protected IndexDelegate getIndexDelegate(){
        return mIndexDelegate;
    }

    public void setInputMusicDir(String musicDir) {
        this.musicInputDir = musicDir;
    }

    /** return true, if filtered */
    protected boolean filter(ExcelRow row){
        List<ExcelCol> columns = row.getColumns();
        String name = columns.get(mIndexDelegate.getNameIndex()).getColumnString();
        String domain = columns.get(mIndexDelegate.getDomainIndex()).getColumnString();
        String mood = columns.get(mIndexDelegate.getMoodIndex()).getColumnString();
        String rhythm = columns.get(mIndexDelegate.getRhythmIndex()).getColumnString();
        if(name == null || name.trim().isEmpty()){
            return true;
        }
        if(domain == null || domain.trim().isEmpty()){
            return true;
        }
        if(mood == null || mood.trim().isEmpty()){
            return true;
        }
        if(rhythm == null || rhythm.trim().isEmpty()){
            return true;
        }
        return false;
    }

    private void writeMappingFile(MusicItem item, List<TimeArea> areas) {
        //write mapping
        String outFile = outDir + File.separator + item.getName() + "___mapping.txt";
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
