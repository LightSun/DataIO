package com.heaven7.java.data.io.music.scan;

import com.google.gson.Gson;
import com.heaven7.java.base.util.Platforms;
import com.heaven7.java.base.util.TextReadHelper;
import com.heaven7.java.data.io.bean.CutConfigBeanV10;
import com.heaven7.java.data.io.bean.CutInfo;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author heaven7
 */
public class MusicCutterScannerV10 extends AbstractMusicCutScanner<CutConfigBeanV10.CutItem> {

    private int mLastAreaType = -1;
    private List<String> mNoTransCutCsvs = new ArrayList<>();
    private List<String> mNoAreaCsvs = new ArrayList<>();

    public MusicCutterScannerV10(String dir) {
        super(dir);
    }
    @Override
    protected List<String> filter(List<String> files) {
        return VisitServices.from(files).filter(new PredicateVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                if (s.endsWith("vamp_bbc-vamp-plugins_bbc-energy_rmsenergy.csv")
                        || s.endsWith("vamp_qm-vamp-plugins_qm-barbeattracker_beatsd.csv")
                        || s.endsWith("vamp_qm-vamp-plugins_qm-segmenter_segmentation.csv")) {
                    return false;
                }
                return true;
            }
        }).getAsList();
    }

    @Override
    protected CutConfigBeanV10.CutItem readCutItem(final String csvPath) {
        String dirName = FileUtils.getFileDir(csvPath, 1, false);
        if (dirName == null || !dirName.endsWith("s")) {
            throw new IllegalStateException("for version 2. csv path must used duration as direct dir. path like '30s', csvPath = " + csvPath);
        }
        final int duration;
        try {
            duration = Integer.valueOf(dirName.substring(0, dirName.length() - 1));
        } catch (NumberFormatException e) {
            throw new IllegalStateException("for version 2. csv path must used duration as direct dir. path like '30s', csvPath = " + csvPath);
        }
        final AtomicBoolean transCutExist = new AtomicBoolean(false);
        final AtomicBoolean speedAreaExist = new AtomicBoolean(true);
        List<CutConfigBeanV10.CutLine> lines = new TextReadHelper<CutConfigBeanV10.CutLine>(new TextReadHelper.Callback<CutConfigBeanV10.CutLine>() {
            @Override
            public CutConfigBeanV10.CutLine parse(String line) {
                CutConfigBeanV10.CutLine cl = new CutConfigBeanV10.CutLine();
                line = line.replace(" ", "");
                String[] strs = line.split(",");
                cl.setCut(Float.parseFloat(strs[0]));
                cl.addFlag(getCutFlags(strs, transCutExist));
                //must call after flag set
                getAreaType(strs, cl, csvPath, speedAreaExist);
                return cl;
            }
        }).read(null, csvPath);
        //末尾需要加一个mark标志, 便于L-M-H区域的处理
        lines.get(lines.size() - 1).addFlag(CutInfo.FLAG_SPEED_AREA_MARKED);


        String fileName = FileUtils.getFileName(csvPath);
        CutConfigBeanV10.CutItem item = new CutConfigBeanV10.CutItem();
        item.setDuration(duration);
        item.setName(UniformNameHelper.uniformSimpleMusicName(fileName));
        item.setCutLines(lines);
        mLastAreaType = - 1;
        //trans_cut and area is wrong return null
        boolean intercept = false;
        if(!transCutExist.get()){
            //intercept = true; // 60s don't have the transition cut.
            if(!mNoTransCutCsvs.contains(csvPath)){
                mNoTransCutCsvs.add(csvPath);
                System.err.println("wrong data for no transition_cut. csvPath = " + csvPath);
            }
        }
        if(!speedAreaExist.get()){
            intercept = true;
            if(!mNoAreaCsvs.contains(csvPath)){
                mNoAreaCsvs.add(csvPath);
                System.err.println("wrong data for head line must has area_type. csvPath = " + csvPath);
            }
        }
        if(intercept){
            return null;
        }
        return item;
    }

    @Override
    protected void writeCutConfig(String targetFilePath, List<CutConfigBeanV10.CutItem> cutItems) {
        CutConfigBeanV10 beanV10 = new CutConfigBeanV10();
        beanV10.setCutItems(cutItems);

        FileUtils.writeTo(targetFilePath, new Gson().toJson(beanV10));
        //write wrong csv.
        writeWrongCsv(targetFilePath, "no_trans_cut.txt", mNoTransCutCsvs);
        writeWrongCsv(targetFilePath, "no_area.txt", mNoAreaCsvs);
    }

    //for debug
    private static void writeWrongCsv(String targetFilePath, String simpleFileName, List<String> csvs) {
        if(!csvs.isEmpty()){
            String content = VisitServices.from(csvs).joinToString(Platforms.getNewLine());
            String wrongCsvPath = new File(targetFilePath).getParent() + File.separator + simpleFileName;
            FileUtils.writeTo(wrongCsvPath, content);
        }
    }

    private void getAreaType(String[] strs, CutConfigBeanV10.CutLine line, String csvPath, AtomicBoolean speedAreaExist) {
        //{L}, {M}, {H}
       // boolean hasLow boolean hasMiddle = false;boolean hasHigh = false;
        int areaType = -1;
        if(!hasStr(strs, "{L}")){
            if(!hasStr(strs, "{M}")){
                if(hasStr(strs, "{H}")){
                    areaType = CutConfigBeanV10.AREA_TYPE_HIGH;
                }
            }else {
                areaType = CutConfigBeanV10.AREA_TYPE_MIDDLE;
            }
        }else {
            areaType = CutConfigBeanV10.AREA_TYPE_LOW;
        }
        if(areaType == -1){
            areaType = mLastAreaType;
            if(mLastAreaType == -1){
                areaType = 0;
                speedAreaExist.compareAndSet(true, false);
                //  throw new RuntimeException("wrong data for head line must has area type. csvPath = " + csvPath);
            }
        }else {
            mLastAreaType = areaType;
            line.addFlag(CutInfo.FLAG_SPEED_AREA_MARKED);
        }
        line.setAreaType(areaType);
    }

    private static byte getCutFlags(String[] strs, AtomicBoolean transCutExist) {
        byte flags = CutInfo.TYPE_INTENSIVE;
        if(hasStr(strs, "^")){
            flags |= CutInfo.TYPE_SPARSE;
        }
        if(hasStr(strs, "@")){
            flags |= CutInfo.FLAG_TRANSITION_CUT;
            transCutExist.compareAndSet(false, true);
        }
        return flags;
    }
    private static boolean hasStr(String[] strs, final String require){
        return VisitServices.from(strs).query(new PredicateVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                return s.equals(require);
            }
        }) != null;
    }

    public static void main(String[] args) {
        new MusicCutterScannerV10("E:\\tmp\\bugfinds\\新版")
                .serialize("E:\\tmp\\bugfinds\\新版\\cut.txt");
    }
}
