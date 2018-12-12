package com.heaven7.java.data.io.music.out;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heaven7.java.data.io.bean.MusicItem;
import com.heaven7.java.data.io.bean.MusicMappingItem;
import com.heaven7.java.data.io.music.Configs;
import com.heaven7.java.data.io.music.PartOutput;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.List;

/**
 * @author heaven7
 */
public class DefalutMusicOutDelegate implements MusicOutDelegate {

    private final Gson mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Override
    public void writePart(final String outDir, final List<MusicItem> items) {
        VisitServices.from(Configs.getAllParts()).fire(new FireVisitor<PartOutput>() {
            @Override
            public Boolean visit(PartOutput part, Object param) {
                List<String> list = VisitServices.from(part.collectDomainWithRhythm(items))
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
    }

    @Override
    public void writeTotal(String outDir, String simpleFileName, List<MusicItem> items) {
        String json = mGson.toJson(items);
        String outJsonFile = outDir + File.separator + simpleFileName + ".json";
        FileUtils.writeTo(outJsonFile, json);
    }

    @Override
    public void writeWarn(String outDir, String simpleFileName, String warnMessages) {
        String warnPath = outDir + File.separator + simpleFileName + "_warn.txt";
        FileUtils.writeTo(warnPath, warnMessages);
    }

    @Override
    public void writeItem(final String outDir, List<MusicItem> items) {
        VisitServices.from(items).fire(new FireVisitor<MusicItem>() {
            @Override
            public Boolean visit(MusicItem item, Object param) {
                //id_duration
                String path = outDir + File.separator + getSingleFilename(item) + ".json";
                FileUtils.writeTo(path, mGson.toJson(item));
                return null;
            }
        });
    }

    @Override
    public void copyValidMusics(String outDir, List<MusicItem> items) {
        final File out = new File(outDir, "musics");
        FileUtils.deleteDir(out);
        out.mkdirs();
        List<MusicMappingItem> maps = VisitServices.from(items).map(new ResultVisitor<MusicItem, MusicMappingItem>() {
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
        //save mapping
        final File file_mapping = new File(outDir, "name_id_mapping.txt");
        FileUtils.writeTo(file_mapping, mGson.toJson(maps));
    }

    private String getSingleFilename(MusicItem item){
        return item.getId() /*+ "_" + item.getDuration() + "s"*/;
    }
}
