package com.heaven7.java.data.io.music.out;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heaven7.java.base.util.Platforms;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.data.io.bean.*;
import com.heaven7.java.data.io.bean.jsonAdapter.MusicItem2JsonAdapter;
import com.heaven7.java.data.io.music.Configs;
import com.heaven7.java.data.io.music.PartOutput;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.music.in.ExcelSource;
import com.heaven7.java.data.io.music.in.MusicNameSource;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.data.io.poi.apply.Cell_StringApplier;
import com.heaven7.java.data.io.poi.apply.Sheet_WidthHeightApplier;
import com.heaven7.java.data.io.poi.apply.TitleRowApplier;
import com.heaven7.java.data.io.poi.write.DefaultExcelWriter;
import com.heaven7.java.data.io.poi.write.ExcelWriter;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.*;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.*;

/**
 * @author heaven7
 */
public class DefalutMusicOutDelegate2 implements MusicOutDelegate2 {

    private final Gson mGson = new GsonBuilder().registerTypeAdapter(MusicItem2.class, new MusicItem2JsonAdapter())
            .disableHtmlEscaping() // if not set: ' -> \u0027
            .create();
    private final List<ServerMapBean> mServeBeans;
    private final MusicNameSource mSortSource;
    private List<ServerPairBean> mServerPairBeans;

    public DefalutMusicOutDelegate2(ExcelSource mServerSource, MusicNameSource mSortSource) {
        this.mServeBeans = readServerConfig(mServerSource);
        this.mSortSource = mSortSource;
    }

    @Override
    public void start(String outDir, List<MusicItem2> items) {
        //attach: display name and singer
        mServerPairBeans = mapServerBean(outDir, items);
        final Map<String, Integer> map = mSortSource.getSortMap();
        if (map != null) {
            Collections.sort(mServerPairBeans, new Comparator<ServerPairBean>() {
                @Override
                public int compare(ServerPairBean o1, ServerPairBean o2) {
                    Integer weight1 = map.get(o1.getName());
                    Integer weight2 = map.get(o2.getName());
                    if (weight1 == null || weight2 == null) {
                        System.out.println();
                    }
                    return Integer.compare(weight1, weight2);
                }
            });
        }
    }

    @Override
    public void end() {

    }

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
                                        return musicItem2.genUniqueId();
                                    }
                                }).getAsList();
                        if (list.isEmpty()) {
                            System.out.println("no items for '" + po.getFormatFilename(duration) + ".json'");
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
                String subDir = "info";
                String infoFile = FileUtils.createFilePath(outDir, "music_info_" + mi.genUniqueId() + ".json", subDir);
                String effectFile = FileUtils.createFilePath(outDir, "effect_" + mi.genUniqueId() + ".json", subDir);
                String transitionFile = FileUtils.createFilePath(outDir, "transition_" + mi.genUniqueId() + ".json", subDir);
                String filterFile = FileUtils.createFilePath(outDir, "filter_" + mi.genUniqueId() + ".json", subDir);

                FileUtils.writeTo(infoFile, mGson.toJson(mi));
                EffectOutItem item = mi.getSpecialEffectItem();
                if (item != null) {
                    FileUtils.writeTo(effectFile, mGson.toJson(item));
                }
                item = mi.getTransitionItem();
                if (item != null) {
                    FileUtils.writeTo(transitionFile, mGson.toJson(item));
                }
                if (!Predicates.isEmpty(mi.getFilterNames())) {
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
                File dst = new File(out, item.genUniqueId() + "." + FileUtils.getFileExtension(item.getRawFile()));
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
        final File file_mapping = new File(outDir, "mapping" + File.separator + "name_id_mapping.txt");
        FileUtils.writeTo(file_mapping, mGson.toJson(maps));
    }

    private void writeServerExcel(String outDir, String simpleFileName, List<MusicItem2> items) {
        // List<ServerPairBean> pairBeans = mapServerBean(outDir, items);
        ExcelWriter.SheetFactory sf = new DefaultExcelWriter().newWorkbook(ExcelWriter.TYPE_XSSF)
                .nesting()
                .newSheet("server-data")
                .apply(new Sheet_WidthHeightApplier(10000, 500, 4))
                .apply(new TitleRowApplier(Arrays.asList("musicid", "name", "timelen", "hashid", "category", "categoryId",
                        "music_info", "effects", "transitions", "filters",
                        "link")))
                .nesting();
        int rowIndex = 1; // f0 is title
        for (ServerPairBean bean : mServerPairBeans) {
            for (MusicItem2 item : bean.items) {
                sf.newRow(rowIndex)
                        .nesting()
                        .newCell(0)
                        .apply(new Cell_StringApplier(bean.getMusicId()))
                        .end()
                        .nesting()
                        .newCell(1)
                        .apply(new Cell_StringApplier(UniformNameHelper.trimPrefixDigital(item.getName())))
                        .end()
                        .nesting()
                        .newCell(2)
                        .apply(new Cell_StringApplier(item.getDuration() + ""))
                        .end()
                        .nesting()
                        .newCell(3)
                        .apply(new Cell_StringApplier(item.getId()))
                        .end()
                        .nesting()
                        .newCell(4)
                        .apply(new Cell_StringApplier(Configs.getCategoryEnglish(item.getCategoryStr())))
                        .end()
                        .nesting()
                        .newCell(5)
                        .apply(new Cell_StringApplier(item.getCategory() + ""))
                        .end()
                        .nesting()
                        .newCell(6)
                        .apply(new Cell_StringApplier(mGson.toJson(item)))
                        .end();
                //effect, transition, filter
                EffectOutItem eoi = item.getSpecialEffectItem();
                if (eoi != null) {
                    sf.nesting().newCell(7).apply(new Cell_StringApplier(mGson.toJson(eoi)));
                } else {
                    sf.nesting().newCell(7).apply(new Cell_StringApplier("{}"));
                }
                eoi = item.getTransitionItem();
                if (eoi != null) {
                    sf.nesting().newCell(8).apply(new Cell_StringApplier(mGson.toJson(eoi)));
                } else {
                    sf.nesting().newCell(8).apply(new Cell_StringApplier("{}"));
                }
                sf.nesting().newCell(9).apply(new Cell_StringApplier(mGson.toJson(item.getFilterNames())));
                //link
                sf.nesting().newCell(10).apply(new Cell_StringApplier(getLink(item.genUniqueId())));
                //add index
                rowIndex++;
            }
        }
        String out = outDir + File.separator + simpleFileName + "_db.xlsx";
        sf.end().end().write(out);
    }

    private List<ServerPairBean> mapServerBean(String outDir, List<MusicItem2> items) {
        final StringBuilder sb_log = new StringBuilder();
        //make the same the music and diff duration for same id.
        List<ServerPairBean> serverBeans = VisitServices.from(items).groupService(new ResultVisitor<MusicItem2, String>() {
            @Override
            public String visit(MusicItem2 musicItem2, Object param) {
                return musicItem2.getName();
            }
        }).map(new MapResultVisitor<String, List<MusicItem2>, ServerPairBean>() {
            @Override
            public ServerPairBean visit(KeyValuePair<String, List<MusicItem2>> pair, Object param) {
                //List<MusicItem2> value = pair.getValue();
                String md5_60 = null;
                for (MusicItem2 mi : pair.getValue()) {
                    if (mi.getDuration() == 60) {
                        md5_60 = mi.getId();
                        break;
                    }
                }
                if (md5_60 == null) {
                    sb_log.append("can't find the md5 for music(60s), name = ")
                            .append(pair.getValue().get(0).getName())
                            .append(Platforms.getNewLine());
                    return ServerPairBean.NULL;
                } else {
                    final String md5 = md5_60;
                    ServerMapBean bean = VisitServices.from(mServeBeans).query(new PredicateVisitor<ServerMapBean>() {
                        @Override
                        public Boolean visit(ServerMapBean serverMapBean, Object param) {
                            return serverMapBean.getMd5().equals(md5);
                        }
                    });
                    if (bean == null) {
                        sb_log.append("can't find the mapping md5 for music, name = ")
                                .append(pair.getValue().get(0).getName())
                                .append(" ,md5 = ").append(md5)
                                .append(Platforms.getNewLine());
                        return ServerPairBean.NULL;
                    }
                    //set singer
                    for (MusicItem2 mi : pair.getValue()) {
                        mi.setSinger(bean.getSinger());
                        mi.setDisplayName(bean.getName());
                        mi.setMusicId(bean.getMusicId());
                    }
                    return new ServerPairBean(bean, pair.getValue());
                }
            }
        }).filter(new PredicateVisitor<ServerPairBean>() {
            @Override
            public Boolean visit(ServerPairBean serverPairBean, Object param) {
                return serverPairBean != ServerPairBean.NULL;
            }
        }).getAsList();

        String targetFile = outDir + File.separator + "mapping" + File.separator + "map_server_bean.txt";
        FileUtils.writeTo(targetFile, sb_log.toString());
        return serverBeans;
    }

    private static List<ServerMapBean> readServerConfig(ExcelSource mServerSource) {
        final int index_musicId = 0;
        final int index_name = 1;
        final int index_singer = 3;
        final int index_md5 = 7;
        final int index_link = 5;
        return VisitServices.from(mServerSource.getRows()).map(new ResultVisitor<ExcelRow, ServerMapBean>() {
            @Override
            public ServerMapBean visit(ExcelRow row, Object param) {
                ServerMapBean bean = new ServerMapBean();
                List<ExcelCol> columns = row.getColumns();
                String musicId = columns.get(index_musicId).getColumnString();
                String name = columns.get(index_name).getColumnString();
                String singer = columns.get(index_singer).getColumnString();
                String md5 = columns.get(index_md5).getColumnString();
                String link = columns.get(index_link).getColumnString();
                bean.setName(name);
                bean.setMusicId(musicId);
                bean.setMd5(md5);
                bean.setLink(link);
                bean.setSinger(singer);
                return bean;
            }
        }).getAsList();
    }

    private static String getLink(String md5) {
        // http://data.xiaoxiekeji.cn/musics/1cbda83aa0ba538b711fcae1dae5a0ea.mp3
        return String.format("http://data.xiaoxiekeji.cn/musics/%s.mp3", md5);
    }

    static class ServerPairBean {
        static final ServerPairBean NULL = new ServerPairBean();
        final ServerMapBean bean;
        final List<MusicItem2> items;

        public ServerPairBean() {
            this(null, null);
        }

        public ServerPairBean(ServerMapBean bean, List<MusicItem2> value) {
            this.bean = bean;
            this.items = value;
        }

        public String getMusicId() {
            return bean.getMusicId();
        }

        public String getName() {
            return items.get(0).getName();
        }
    }
}
