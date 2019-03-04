package com.heaven7.java.data.io.music.out;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.data.io.bean.*;
import com.heaven7.java.data.io.bean.jsonAdapter.MusicItem2JsonAdapter;
import com.heaven7.java.data.io.music.Configs;
import com.heaven7.java.data.io.music.PartOutput;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.poi.apply.Cell_StringApplier;
import com.heaven7.java.data.io.poi.apply.Sheet_WidthHeightApplier;
import com.heaven7.java.data.io.poi.apply.TitleRowApplier;
import com.heaven7.java.data.io.poi.write.DefaultExcelWriter;
import com.heaven7.java.data.io.poi.write.ExcelWriter;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.MapFireVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.*;

/**
 * @author heaven7
 */
public class DefalutMusicOutDelegate2 implements MusicOutDelegate2 {

    private final Gson mGson = new GsonBuilder().registerTypeAdapter(MusicItem2.class, new MusicItem2JsonAdapter()).create();

    @Override
    public void writePart(final String outDir, final List<MusicItem2> items) {
        //domain_music_thythm_duration
        final List<PartOutput> parts = Configs.getPartsOfDomainRhythm();
        VisitServices.from(items).groupService(new ResultVisitor<MusicItem2, Integer>() {
            @Override
            public Integer visit(MusicItem2 musicItem2, Object param) {
                return musicItem2.getDuration();
            }
        }).mapPair().fire(new FireVisitor<KeyValuePair<Integer, List<MusicItem2>>>() {
            @Override
            public Boolean visit(final KeyValuePair<Integer, List<MusicItem2>> pair, Object param) {
                final Integer duration = pair.getKey();
                VisitServices.from(parts).fire(new FireVisitor<PartOutput>() {
                    @Override
                    public Boolean visit(PartOutput po, Object param) {
                        List<String> list = VisitServices.from(po.collectDomainWithRhythmWithoutDuration(pair.getValue()))
                                .map(new ResultVisitor<MusicItem2, String>() {
                                    @Override
                                    public String visit(MusicItem2 musicItem2, Object param) {
                                        return musicItem2.getId();
                                    }
                                }).getAsList();
                        if (list.isEmpty()) {
                            System.out.println("no items for '"+ po.getFormatFilename(duration) + ".json'");
                            return false;
                        }
                        String partPath = outDir + File.separator + "parts" + File.separator + po.getFormatFilename(duration) + ".json";
                        FileUtils.writeTo(partPath, mGson.toJson(list));
                        return null;
                    }
                });
                return null;
            }
        });
    }

    @Override
    public void writeTotal(String outDir, String simpleFileName, List<MusicItem2> items) {
        String json = mGson.toJson(items);
        String outJsonFile = outDir + File.separator + simpleFileName + ".json";
        FileUtils.writeTo(outJsonFile, json);

        //write server excel
        writeServerExcel(outDir, simpleFileName, items);
    }

    @Override
    public void writeWarn(String outDir, String simpleFileName, String warnMessages) {
        System.out.println("writeWarn: >>>\r\n" + warnMessages);
    }

    @Override
    public void writeItem(final String outDir, List<MusicItem2> items) {
        VisitServices.from(items).fire(new FireVisitor<MusicItem2>() {
            @Override
            public Boolean visit(MusicItem2 mi, Object param) {
                String infoFile = outDir + File.separator + "music_info_" + mi.genUniqueId() + ".json";
                String effectFile = outDir + File.separator + "effect_" + mi.genUniqueId() + ".json";
                String transitionFile = outDir + File.separator + "transition_" + mi.genUniqueId() + ".json";
                String filterFile = outDir + File.separator + "filter_" + mi.genUniqueId() + ".json";
                FileUtils.writeTo(infoFile, mGson.toJson(mi));
                EffectOutItem item = mi.getSpecialEffectItem();
                if(item != null){
                    FileUtils.writeTo(effectFile, mGson.toJson(item));
                }

                item = mi.getTransitionItem();
                if(item != null){
                    FileUtils.writeTo(transitionFile, mGson.toJson(item));
                }
                if(!Predicates.isEmpty(mi.getFilterNames())){
                    FileUtils.writeTo(filterFile, mGson.toJson(mi.getFilterNames()));
                }
                return null;
            }
        });
    }
    @Override
    public void copyValidMusics(String outDir, List<MusicItem2> items) {
        final File out = new File(outDir, "musics");
        FileUtils.deleteDir(out);
        out.mkdirs();
        List<MusicMappingItem> maps = VisitServices.from(items).map(new ResultVisitor<MusicItem2, MusicMappingItem>() {
            @Override
            public MusicMappingItem visit(MusicItem2 item, Object param) {
                Float maxTime = item.getMaxTime();
                File dst = new File(out, item.getId() + "." + FileUtils.getFileExtension(item.getRawFile()));
                MusicMappingItem mmi = new MusicMappingItem();
                mmi.setMusicName(item.getName());
                mmi.setId(item.getId());
                mmi.setFullId(dst.getAbsolutePath());
                mmi.setFilename(item.getRawFile());
                mmi.setDuration(maxTime);
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

    private void writeServerExcel(String outDir, String simpleFileName, List<MusicItem2> items) {
        ExcelWriter.SheetFactory sf = new DefaultExcelWriter().newWorkbook(ExcelWriter.TYPE_XSSF)
                .nesting()
                .newSheet("server-data")
                .apply(new Sheet_WidthHeightApplier(10000, 500, 4))
                .apply(new TitleRowApplier(Arrays.asList("name", "timelen", "hashid", "category","categoryId","music_info",
                        "effects", "transitions", "filters")))
                .nesting();
        int rowIndex = 1;
        for(MusicItem2 item : items){
            sf.newRow(rowIndex)
                    .nesting()
                    .newCell(0)
                    .apply(new Cell_StringApplier(UniformNameHelper.trimPrefixDigital(item.getName())))
                    .end()
                    .nesting()
                    .newCell(1)
                    .apply(new Cell_StringApplier(item.getDuration() + ""))
                    .end()
                    .nesting()
                    .newCell(2)
                    .apply(new Cell_StringApplier(item.getId()))
                    .end()
                    .nesting()
                    .newCell(3)
                    .apply(new Cell_StringApplier(Configs.getCategoryEnglish(item.getCategoryStr())))
                    .end()
                    .nesting()
                    .newCell(4)
                    .apply(new Cell_StringApplier(item.getCategory() + ""))
                    .end()
                    .nesting()
                    .newCell(5)
                    .apply(new Cell_StringApplier(mGson.toJson(item)))
                    .end();
            //effect, transition, filter
            EffectOutItem eoi = item.getSpecialEffectItem();
            if(eoi != null){
                sf.nesting().newCell(6).apply(new Cell_StringApplier(mGson.toJson(eoi)));
            }else {
                sf.nesting().newCell(6).apply(new Cell_StringApplier("{}"));
            }
            eoi = item.getTransitionItem();
            if(eoi != null){
                sf.nesting().newCell(7).apply(new Cell_StringApplier(mGson.toJson(eoi)));
            }else {
                sf.nesting().newCell(7).apply(new Cell_StringApplier("{}"));
            }
            sf.nesting().newCell(8).apply(new Cell_StringApplier(mGson.toJson(item.getFilterNames())));
            //add index
            rowIndex ++;
        }

        String out = outDir + File.separator + simpleFileName + "_db.xlsx";
        sf.end().end().write(out);
    }

}
