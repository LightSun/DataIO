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

    @Test //contains old music cut source.
    public void test1(){
        //new MultiMusicCutSource(oldMusicCutSrc, source)
    }

    @Test
    public void test2(){
       // String oldMusicCutFile = "E:\\tmp\\bugfinds\\music_cuts2\\1212\\cuts_uniform.txt";
        String newMusicCutFile = "E:\\tmp\\bugfinds\\music_cut3\\cut.txt";
        String musicDir = "E:\\tmp\\bugfinds\\right_music2";
        String outDir = "E:\\tmp\\bugfinds\\新版\\out";

        String serverConfigFile = "E:\\tmp\\bugfinds\\server_mapping.xlsx";
        String musicNameTable = "E:\\tmp\\bugfinds\\music_name_table.xlsx";
        String mainExcelPath = "E:\\tmp\\bugfinds\\测试004.xlsx";

        MusicOutDelegate2 delegate2 = new DefalutMusicOutDelegate2(new SimpleExcelSource(
                new ExcelHelper.Builder()
                        .setSheetName("工作表3")
                        .setSkipToRowIndex(1)
                        .setUseXlsx(true)
                        .setExcelPath(serverConfigFile)
                        .build()));

        ExcelHelper musicNameTables = new ExcelHelper.Builder()
                                .setUseXlsx(true)
                                .setExcelPath(musicNameTable)
                                .setSkipToRowIndex(1)
                                .setSheetName("线上音乐文件")
                                .build();

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
                                .setExcelPath("E:\\tmp\\bugfinds\\music9.xlsx")
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
                .setMusicNameSource(new ExcelMusicNameSource(musicNameTables))
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
}
