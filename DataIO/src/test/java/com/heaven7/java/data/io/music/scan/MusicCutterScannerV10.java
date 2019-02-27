package com.heaven7.java.data.io.music.scan;

import com.google.gson.Gson;
import com.heaven7.java.base.util.TextReadHelper;
import com.heaven7.java.data.io.bean.CutConfigBeanV10;
import com.heaven7.java.data.io.bean.CutInfo;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.List;

/**
 * @author heaven7
 */
public class MusicCutterScannerV10 extends AbstractMusicCutScanner<CutConfigBeanV10.CutItem> {

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
    protected CutConfigBeanV10.CutItem readCutItem(String csvPath) {
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
        List<CutConfigBeanV10.CutLine> lines = new TextReadHelper<CutConfigBeanV10.CutLine>(new TextReadHelper.Callback<CutConfigBeanV10.CutLine>() {
            @Override
            public CutConfigBeanV10.CutLine parse(String line) {
                CutConfigBeanV10.CutLine cl = new CutConfigBeanV10.CutLine();
                line = line.replace(" ", "");
                String[] strs = line.split(",");
                cl.setCut(Float.parseFloat(strs[0]));
                cl.setFlags(strs.length >=3 && strs[2].equals("^") ? CutInfo.TYPE_INTENSIVE | CutInfo.TYPE_SPARSE : CutInfo.TYPE_INTENSIVE);
                return cl;
            }
        }).read(null, csvPath);
        //
        String fileName = FileUtils.getFileName(csvPath);
        CutConfigBeanV10.CutItem item = new CutConfigBeanV10.CutItem();
        item.setDuration(duration);
        item.setName(UniformNameHelper.uniformSimpleMusicName(fileName));
        item.setCutLines(lines);
        return item;
    }

    @Override
    protected void writeCutConfig(String targetFilePath, List<CutConfigBeanV10.CutItem> cutItems) {
        CutConfigBeanV10 beanV10 = new CutConfigBeanV10();
        beanV10.setCutItems(cutItems);

        FileUtils.writeTo(targetFilePath, new Gson().toJson(beanV10));
    }

    public static void main(String[] args) {
        //
        new MusicCutterScannerV10("E:\\tmp\\bugfinds\\新版").serialize("E:\\tmp\\bugfinds\\新版\\cut.txt");
    }
}
