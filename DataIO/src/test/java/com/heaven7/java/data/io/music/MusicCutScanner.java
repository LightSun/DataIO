package com.heaven7.java.data.io.music;

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
 * @author heaven7
 */
public class MusicCutScanner {

    private final String dir;

    public MusicCutScanner(String dir) {
        this.dir = dir;
    }

    public void serialize(String targetFilePath){
        List<String> files = new ArrayList<>();
        FileUtils.getFiles(new File(dir), "csv", new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isHidden();
            }
        }, files);
        files = filter(files);
        List<CutConfigBean.CutItem> items = VisitServices.from(files).map(new ResultVisitor<String, CutConfigBean.CutItem>() {
            @Override
            public CutConfigBean.CutItem visit(String csvPath, Object param) {
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
        }).getAsList();
        CutConfigBean bean = new CutConfigBean();
        bean.setCutItems(items);
        FileUtils.writeTo(targetFilePath, new Gson().toJson(bean));
    }

    protected List<String> filter(List<String> files) {
        return files;
    }

    public static void main(String[] args) {
      /*  String dir = "E:\\tmp\\bugfinds\\music_cuts";
        String configPath = "E:\\tmp\\bugfinds\\music_cuts\\cuts.txt";
        new MusicCutScanner(dir).serialize(configPath);*/
        String dir = "E:\\tmp\\bugfinds\\music_cuts\\vida_demo_1122";
        String configPath = "E:\\tmp\\bugfinds\\music_cuts\\vida_demo_1122\\cuts.txt";
        new MusicCutScanner(dir).serialize(configPath);
    }
}
