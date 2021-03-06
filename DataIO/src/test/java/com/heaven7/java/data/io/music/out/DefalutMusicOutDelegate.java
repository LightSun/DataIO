package com.heaven7.java.data.io.music.out;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heaven7.java.data.io.bean.MusicItem;
import com.heaven7.java.data.io.bean.MusicMappingItem;
import com.heaven7.java.data.io.bean.PartConfig;
import com.heaven7.java.data.io.bean.PartItem;
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
public class DefalutMusicOutDelegate implements MusicOutDelegate1 {

    private final Gson mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Override
    public void writePart(final String outDir, final List<MusicItem> items) {
        writeTotalCategories(outDir, items);
        //old.
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

        //write server excel
        writeServerExcel(outDir, simpleFileName, items);
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

    private void writeTotalCategories(String outDir, final List<MusicItem> items) {
        final Map<String, List<PartItem>> configMap = new HashMap<>();
        PartConfig pc = new PartConfig();
        pc.setParts(configMap);

        VisitServices.from(Configs.getDomainParts()).fire(new FireVisitor<PartOutput>() {
            @Override
            public Boolean visit(final PartOutput part, Object param) {
                final List<PartItem> pitems = new ArrayList<>();
                configMap.put(part.getPartDomain(), pitems);

                final List<MusicItem> domainItems = part.collectDomain(items);
                List<PartOutput> prs = Configs.getPartsOfPropertyRhythm();
                VisitServices.from(prs).fire(new FireVisitor<PartOutput>() {
                    @Override
                    public Boolean visit(final PartOutput part2, Object param) {
                        //data
                        PartItem item = new PartItem();
                        item.setProperty(part2.getPartProperty());
                        item.setRhythm(part2.getPartRhythm());
                        final Map<String, List<String>> map = new HashMap<>();
                        item.setIdMap(map);

                        //transform
                        VisitServices.from(part2.collectPropertyRhythm(domainItems))
                                .groupService(new ResultVisitor<MusicItem, Integer>() {
                                    @Override
                                    public Integer visit(MusicItem item, Object param) {
                                        return item.getDuration();
                                    }
                                }).fire(new MapFireVisitor<Integer, List<MusicItem>>() {
                            @Override
                            public Boolean visit(KeyValuePair<Integer, List<MusicItem>> pair, Object param) {
                                List<String> ids = VisitServices.from(pair.getValue()).map(new ResultVisitor<MusicItem, String>() {
                                    @Override
                                    public String visit(MusicItem item, Object param) {
                                        return item.getId();
                                    }
                                }).getAsList();
                                if(!ids.isEmpty()){
                                    map.put(pair.getKey() + "", ids);
                                }
                                return null;
                            }
                        });
                        if(!map.isEmpty()){
                            pitems.add(item);
                        }
                        return null;
                    }
                });
                return false;
            }
        });
        String path = outDir + File.separator + "categories.json";
        FileUtils.writeTo(path, mGson.toJson(pc));
    }

    private void writeServerExcel(String outDir, String simpleFileName, List<MusicItem> items) {
        ExcelWriter.SheetFactory sf = new DefaultExcelWriter().newWorkbook(ExcelWriter.TYPE_XSSF)
                .nesting()
                .newSheet("server-data")
                .apply(new Sheet_WidthHeightApplier(10000, 500, 4))
                .apply(new TitleRowApplier(Arrays.asList("name", "timelen", "hashid", "category","categoryId","cuts")))
                .nesting();
        int rowIndex = 1;
        for(MusicItem item : items){
            sf.newRow(rowIndex)
                    .nesting()
                        .newCell(0)
                        .apply(new Cell_StringApplier(UniformNameHelper.trimPrefixDigital(item.getName())))
                    .end()
                    .nesting()
                        .newCell(1)
                        .apply(new Cell_StringApplier(item.getMaxTime() + ""))
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
                        .apply(new Cell_StringApplier(item.getCategory() +""))
                    .end()
                    .nesting()
                        .newCell(5)
                        .apply(new Cell_StringApplier(mGson.toJson(item)))
                    .end();
            rowIndex ++;
        }

        String out = outDir + File.separator + simpleFileName + "_db.xlsx";
        sf.end().end().write(out);
    }
}
