package com.heaven7.java.data.io.music;

import com.google.gson.Gson;
import com.heaven7.java.base.util.TextReadHelper;
import com.heaven7.java.data.io.bean.CutConfigBean;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.List;

/**
 * @author heaven7
 */
public class MusicCutScanner {

    private final String dir;

    public MusicCutScanner(String dir) {
        this.dir = dir;
    }

    public void serialize(String targetFilePath){
        List<String> files = FileUtils.getFiles(new File(dir), "csv");
        List<CutConfigBean.CutItem> items = VisitServices.from(files).map(new ResultVisitor<String, CutConfigBean.CutItem>() {
            @Override
            public CutConfigBean.CutItem visit(String csvPath, Object param) {
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
            }
        }).getAsList();
        CutConfigBean bean = new CutConfigBean();
        bean.setCutItems(items);
        FileUtils.writeTo(targetFilePath, new Gson().toJson(bean));
    }

    public static void main(String[] args) {
        String dir = "E:\\tmp\\bugfinds\\music_cuts";
        String configPath = "E:\\tmp\\bugfinds\\music_cuts\\cuts.txt";
        new MusicCutScanner(dir).serialize(configPath);
    }
}
