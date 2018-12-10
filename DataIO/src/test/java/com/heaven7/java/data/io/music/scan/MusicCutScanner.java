package com.heaven7.java.data.io.music.scan;

import com.google.gson.Gson;
import com.heaven7.java.base.util.TextReadHelper;
import com.heaven7.java.data.io.bean.CutConfigBean;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描音乐的csv成cutx.txt
 * @author heaven7
 */
public class MusicCutScanner extends AbstractMusicCutScanner<CutConfigBean.CutItem> {

    public MusicCutScanner(String dir) {
        super(dir);
    }

    protected void writeCutConfig(String targetFilePath, List<CutConfigBean.CutItem> items) {
        CutConfigBean bean = new CutConfigBean();
        bean.setCutItems(items);
        FileUtils.writeTo(targetFilePath, new Gson().toJson(bean));
    }

    /**
     * read the cut item from csv.
     * @param csvPath the music info path
     * @return the cut item.
     */
    protected CutConfigBean.CutItem readCutItem(String csvPath){
        try {
            String fileName = FileUtils.getFileName(csvPath);
            TextReadHelper<Float> readHelper = new TextReadHelper<>(new TextReadHelper.Callback<Float>() {
                @Override
                public Float parse(String line) {
                    return Float.valueOf(line.split(",")[0]);
                }
            });
            List<Float> floats = readHelper.read(null, csvPath);
            CutConfigBean.CutItem item = new CutConfigBean.CutItem();
            item.setName(fileName);
            item.setCuts(VisitServices.from(floats).joinToString(","));
            return item;
        }catch (Exception e){
            throw new RuntimeException(csvPath, e);
        }
    }

    protected List<String> filter(List<String> files) {
        return files;
    }

    public static void main(String[] args) {
      /*  String dir = "E:\\tmp\\bugfinds\\music_cuts";
        String configPath = "E:\\tmp\\bugfinds\\music_cuts\\cuts.txt";
        new MusicCutScanner(dir).serialize(configPath);*/
       // String dir = "E:\\tmp\\bugfinds\\music_cuts\\vida_demo_1122";
       // String configPath = "E:\\tmp\\bugfinds\\music_cuts\\vida_demo_1122\\cuts.txt";
        String dir = "E:\\tmp\\bugfinds\\music_cuts2\\1208\\60s";
        String configPath = "E:\\tmp\\bugfinds\\music_cuts2\\1208\\cuts.txt";
        new MusicCutScanner(dir).serialize(configPath);
    }
}
