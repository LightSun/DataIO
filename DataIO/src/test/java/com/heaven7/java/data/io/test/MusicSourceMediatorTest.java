package com.heaven7.java.data.io.test;

import com.heaven7.java.data.io.music.adapter.IndexDelegateV3;
import com.heaven7.java.data.io.music.adapter.MusicSourceMediator;
import com.heaven7.java.data.io.music.in.*;
import com.heaven7.java.data.io.music.out.DefalutMusicOutDelegate2;
import com.heaven7.java.data.io.music.out.FileLogWriter;
import com.heaven7.java.data.io.music.out.MusicOutDelegate2;
import com.heaven7.java.data.io.music.transfer.TransitionTransfer;
import com.heaven7.java.data.io.music.transfer.FilterTransfer;
import com.heaven7.java.data.io.music.transfer.SpeedEffectTransfer;
import com.heaven7.java.data.io.music.transfer.TransitionCutTransfer;
import com.heaven7.java.data.io.poi.ExcelHelper;
import org.junit.Test;

/**
 * @author heaven7
 */
public class MusicSourceMediatorTest {

    @Test //contains old music cut pm.
    public void test1(){
        //new MultiMusicCutSource(oldMusicCutSrc, pm)
    }

    @Test //15s + 60s
    public void test2(){
       // String oldMusicCutFile = "E:\\tmp\\bugfinds\\music_cuts2\\1212\\cuts_uniform.txt";
        String newMusicCutFile = "E:\\tmp\\bugfinds\\music_cut3\\cut.txt";
        String musicDir = "E:\\tmp\\bugfinds\\right_music2";
        String outDir = "E:\\tmp\\bugfinds\\新版\\out";

        String serverConfigFile = "E:\\tmp\\bugfinds\\server_mapping.xlsx";
        String musicNameTable = "E:\\tmp\\bugfinds\\music_name_table.xlsx";
        String mainExcelPath = "E:\\tmp\\bugfinds\\测试008.xlsx";
        String oldExcelPath = "E:\\tmp\\bugfinds\\music9.xlsx";
        String sortExcelPath = "E:\\tmp\\bugfinds\\音乐排序.xlsx";

        ExcelHelper sortNameTable = new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(sortExcelPath)
                .setSkipToRowIndex(0)
                .setSheetName("工作表 2")
                .build();

        ExcelHelper musicNameTables = new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(musicNameTable)
                .setSkipToRowIndex(1)
                .setSheetName("线上音乐文件")
                .build();
        ExcelMusicNameSource nameSource = new ExcelMusicNameSource(musicNameTables, sortNameTable);

        MusicOutDelegate2 delegate2 = new DefalutMusicOutDelegate2(new SimpleExcelSource(
                new ExcelHelper.Builder()
                        .setSheetName("工作表3")
                        .setSkipToRowIndex(1)
                        .setUseXlsx(true)
                        .setExcelPath(serverConfigFile)
                        .build()), nameSource);


        SimpleMusicCutSource source = new SimpleMusicCutSource(newMusicCutFile);

        ExcelSource filterSource =
                new SimpleExcelSource(
                        new ExcelHelper.Builder()
                                .setUseXlsx(true)
                                .setExcelPath(mainExcelPath)
                                .setSkipToRowIndex(2)
                                .setSheetName("滤镜 - 表格 1-1-1-1-1")
                                .build());
        ExcelSource transitionSource = new SimpleExcelSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(mainExcelPath)
                .setSkipToRowIndex(2)
                .setSheetName("转场 - 表格 1-1-1-1")
                .build());
        ExcelSource effectSource = new SimpleExcelSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(mainExcelPath)
                .setSkipToRowIndex(2)
                .setSheetName("特效 - 表格 1-1")
                .build());
        ExcelSource standSource = new SimpleExcelSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(mainExcelPath)
                .setSkipToRowIndex(1)
                .setSheetName("切点 - 表格 2-1")
                .build());
        ExcelSource oldStandSource =
                new SimpleExcelSource(
                        new ExcelHelper.Builder()
                                .setUseXlsx(true)
                                .setExcelPath(oldExcelPath)
                                .setSkipToRowIndex(2)
                                .setSheetName("sheet3")
                                .build());


        EffectMappingSource effectMappingSource = new ExcelEffectMappingSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath("E:\\tmp\\bugfinds\\特效1.1.xlsx")
                .setSkipToRowIndex(1)
                .setSheetName("工作表 1")
                .build());
        //--------------------------------------------
        MusicSourceMediator mediator = new MusicSourceMediator.Builder()
                .setExcelSources(new ExcelSources.Builder()
                        .setTransCutSource(new TransferCutSource(source.getBean()))
                        .setFilterSource(filterSource)
                        .setTransitionSource(transitionSource)
                        .setSpeedEffectSource(effectSource)
                        .setStandSource(standSource)
                        .setOldStandSource(oldStandSource)
                        .build())
                .setMusicCutSource(source)
                .setSpeedAreaSource(new SimpleSpeedAreaSource(source.getBean()))
                .setMusicSource(new SimpleMusicSource(musicDir))
                .setMusicNameSource(nameSource)
                .setEffectMappingSource(effectMappingSource)

                .setOutDir(outDir)
                .setMusicOutDelegate(delegate2)
                .setIndexDelegate(new IndexDelegateV3())

                .setTransitionTransfer(new TransitionTransfer())
                .setFilterTransfer(new FilterTransfer())
                .setSpeedEffectTransfer(new SpeedEffectTransfer())
                .setTransitionCutTransfer(new TransitionCutTransfer())
                .setForceUseNewSpeedArea(true)
                .setLogWriter(new FileLogWriter(outDir))
                .build();

        mediator.normalize();
    }
    @Test //只输出部分音乐。base on musiclist.txt
    public void test3(){
        // String oldMusicCutFile = "E:\\tmp\\bugfinds\\music_cuts2\\1212\\cuts_uniform.txt";
        // music name
        String newMusicCutFile = "E:\\tmp\\bugfinds\\music_cut3\\cut.txt";
        String musicDir = "E:\\tmp\\bugfinds\\right_music2";
        String outDir = "E:\\tmp\\bugfinds\\新版\\out_part";

        //String musicNameTable = "E:\\tmp\\bugfinds\\musiclist.txt";
        //String musicNameTable = "E:\\tmp\\bugfinds\\本地音乐列表.xlsx";
        String musicNameTable = "E:\\tmp\\bugfinds\\musiclist2.txt";
        String serverConfigFile = "E:\\tmp\\bugfinds\\server_mapping.xlsx";
        String mainExcelPath = "E:\\tmp\\bugfinds\\测试009_test.xlsx";
        String oldExcelPath = "E:\\tmp\\bugfinds\\music9.xlsx";

        SimpleMusicNameSource nameSource = new SimpleMusicNameSource(musicNameTable);
       /* ExcelMusicNameSource2 nameSource = new ExcelMusicNameSource2(new ExcelHelper.Builder()
                .setSheetName("工作表 2")
                .setSkipToRowIndex(0)
                .setUseXlsx(true)
                .setExcelPath(musicNameTable)
                .build(), null);*/
        MusicOutDelegate2 delegate2 = new DefalutMusicOutDelegate2(new SimpleExcelSource(
                new ExcelHelper.Builder()
                        .setSheetName("工作表3")
                        .setSkipToRowIndex(1)
                        .setUseXlsx(true)
                        .setExcelPath(serverConfigFile)
                        .build()), nameSource);

        SimpleMusicCutSource source = new SimpleMusicCutSource(newMusicCutFile);

        ExcelSource filterSource =
                new SimpleExcelSource(
                        new ExcelHelper.Builder()
                                .setUseXlsx(true)
                                .setExcelPath(mainExcelPath)
                                .setSkipToRowIndex(2)
                                .setSheetName("滤镜 - 表格 1-1-1-1-1")
                                .build());
        ExcelSource transitionSource = new SimpleExcelSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(mainExcelPath)
                .setSkipToRowIndex(2)
                .setSheetName("转场 - 表格 1-1-1-1")
                .build());
        ExcelSource effectSource = new SimpleExcelSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(mainExcelPath)
                .setSkipToRowIndex(2)
                .setSheetName("特效 - 表格 1-1")
                .build());
        ExcelSource standSource = new SimpleExcelSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(mainExcelPath)
                .setSkipToRowIndex(1)
                .setSheetName("切点 - 表格 2-1")
                .build());
        ExcelSource oldStandSource =
                new SimpleExcelSource(
                        new ExcelHelper.Builder()
                                .setUseXlsx(true)
                                .setExcelPath(oldExcelPath)
                                .setSkipToRowIndex(2)
                                .setSheetName("sheet3")
                                .build());


        EffectMappingSource effectMappingSource = new ExcelEffectMappingSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath("E:\\tmp\\bugfinds\\特效1.1.xlsx")
                .setSkipToRowIndex(1)
                .setSheetName("工作表 1")
                .build());
        //--------------------------------------------
        MusicSourceMediator mediator = new MusicSourceMediator.Builder()
                .setExcelSources(new ExcelSources.Builder()
                        .setTransCutSource(new TransferCutSource(source.getBean()))
                        .setFilterSource(filterSource)
                        .setTransitionSource(transitionSource)
                        .setSpeedEffectSource(effectSource)
                        .setStandSource(standSource)
                        .setOldStandSource(oldStandSource)
                        .build())
                .setMusicCutSource(source)
                .setSpeedAreaSource(new SimpleSpeedAreaSource(source.getBean()))
                .setMusicSource(new SimpleMusicSource(musicDir))
                .setMusicNameSource(nameSource)
                .setEffectMappingSource(effectMappingSource)

                .setOutDir(outDir)
                .setMusicOutDelegate(delegate2)
                .setIndexDelegate(new IndexDelegateV3())

                .setTransitionTransfer(new TransitionTransfer())
                .setFilterTransfer(new FilterTransfer())
                .setSpeedEffectTransfer(new SpeedEffectTransfer())
                .setTransitionCutTransfer(new TransitionCutTransfer())
                .setForceUseNewSpeedArea(true)
                .setLogWriter(new FileLogWriter(outDir))
                .build();

        mediator.normalize();
    }


    @Test //全时长
    public void test4(){
        // String oldMusicCutFile = "E:\\tmp\\bugfinds\\music_cuts2\\1212\\cuts_uniform.txt";
        String newMusicCutFile = "E:\\tmp\\bugfinds\\music_cut3\\cut.txt";
        String musicDir = "E:\\tmp\\bugfinds\\right_music2";
        String outDir = "E:\\tmp\\bugfinds\\全时长\\out";

        String serverConfigFile = "E:\\tmp\\bugfinds\\server_mapping.xlsx";
        String musicNameTable = "E:\\tmp\\bugfinds\\music_name_table.xlsx";
        String mainExcelPath = "E:\\tmp\\bugfinds\\测试008.xlsx";
        String oldExcelPath = "E:\\tmp\\bugfinds\\music9.xlsx";
        String sortExcelPath = "E:\\tmp\\bugfinds\\音乐排序.xlsx";
        //full-time paramaters
        String fullTimeCutFile = "E:\\tmp\\bugfinds\\music_cut_全时长\\cut.txt";
        String fullTimeMusicNameTable = "E:\\tmp\\bugfinds\\全时长\\music_list_fulltime.txt";

        ExcelHelper sortNameTable = new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(sortExcelPath)
                .setSkipToRowIndex(0)
                .setSheetName("工作表 2")
                .build();

        ExcelHelper musicNameTables = new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(musicNameTable)
                .setSkipToRowIndex(1)
                .setSheetName("线上音乐文件")
                .build();
        ExcelMusicNameSource nameSource = new ExcelMusicNameSource(musicNameTables, sortNameTable);

        DefalutMusicOutDelegate2 outDelegate = new DefalutMusicOutDelegate2(new SimpleExcelSource(
                new ExcelHelper.Builder()
                        .setSheetName("工作表3")
                        .setSkipToRowIndex(1)
                        .setUseXlsx(true)
                        .setExcelPath(serverConfigFile)
                        .build()), nameSource);
       // DefalutMusicOutDelegate3.setFilterDuration(0); //0 s means full-time


        SimpleMusicCutSource cutSource = new SimpleMusicCutSource(newMusicCutFile);
        SimpleMusicCutSource fullTimeCutSource = new SimpleMusicCutSource(fullTimeCutFile);

        ExcelSource filterSource =
                new SimpleExcelSource(
                        new ExcelHelper.Builder()
                                .setUseXlsx(true)
                                .setExcelPath(mainExcelPath)
                                .setSkipToRowIndex(2)
                                .setSheetName("滤镜 - 表格 1-1-1-1-1")
                                .build());
        ExcelSource transitionSource = new SimpleExcelSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(mainExcelPath)
                .setSkipToRowIndex(2)
                .setSheetName("转场 - 表格 1-1-1-1")
                .build());
        ExcelSource effectSource = new SimpleExcelSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(mainExcelPath)
                .setSkipToRowIndex(2)
                .setSheetName("特效 - 表格 1-1")
                .build());
        ExcelSource standSource = new SimpleExcelSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(mainExcelPath)
                .setSkipToRowIndex(1)
                .setSheetName("切点 - 表格 2-1")
                .build());
        ExcelSource oldStandSource =
                new SimpleExcelSource(
                        new ExcelHelper.Builder()
                                .setUseXlsx(true)
                                .setExcelPath(oldExcelPath)
                                .setSkipToRowIndex(2)
                                .setSheetName("sheet3")
                                .build());


        EffectMappingSource effectMappingSource = new ExcelEffectMappingSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath("E:\\tmp\\bugfinds\\特效1.1.xlsx")
                .setSkipToRowIndex(1)
                .setSheetName("工作表 1")
                .build());
        //--------------------------------------------
        MusicSourceMediator mediator = new MusicSourceMediator.Builder()
                .setExcelSources(new ExcelSources.Builder()
                        .setTransCutSource(new TransferCutSource(cutSource.getBean()))
                        .setFilterSource(filterSource)
                        .setTransitionSource(transitionSource)
                        .setSpeedEffectSource(effectSource)
                        .setStandSource(standSource)
                        .setOldStandSource(oldStandSource)
                        .build())
                .setMusicCutSource(cutSource)
                .setSpeedAreaSource(new SimpleSpeedAreaSource(cutSource.getBean()))
                .setMusicSource(new SimpleMusicSource(musicDir))
                .setMusicNameSource(nameSource)
                .setEffectMappingSource(effectMappingSource)

                .setOutDir(outDir)
                .setMusicOutDelegate(outDelegate)
                .setIndexDelegate(new IndexDelegateV3())

                .setFullTimeCutSource(fullTimeCutSource)
               // .setFullTimeMusicNameSource(new SimpleMusicNameSource(fullTimeMusicNameTable))
                .setFullTimeMusicNameSource(nameSource)
                .setFullTimeSpeedAreaSource(new SimpleSpeedAreaSource(fullTimeCutSource.getBean()))

                .setTransitionTransfer(new TransitionTransfer())
                .setFilterTransfer(new FilterTransfer())
                .setSpeedEffectTransfer(new SpeedEffectTransfer())
                .setTransitionCutTransfer(new TransitionCutTransfer())
                .setForceUseNewSpeedArea(true)
                .setLogWriter(new FileLogWriter(outDir))
                .build();

        mediator.normalize();
    }
}
