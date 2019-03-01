package com.heaven7.java.data.io.test;

import com.heaven7.java.data.io.music.adapter.IndexDelegateV3;
import com.heaven7.java.data.io.music.adapter.MusicSourceMediator;
import com.heaven7.java.data.io.music.in.*;
import com.heaven7.java.data.io.music.out.DefalutMusicOutDelegate2;
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

    @Test
    public void testWhole(){
        String outDir = "";
        String oldMusicCutFile = "";
        String newMusicCutFile = "";
        MusicOutDelegate2 delegate2 = null;

        SimpleMusicCutSource source = new SimpleMusicCutSource(newMusicCutFile);
        //--------------------------------------------
        MusicSourceMediator mediator = new MusicSourceMediator.Builder()
                .setExcelSources(new ExcelSources.Builder()
                        .setTransCutSource(new TransferCutSource(source.getBean()))
                       //TODO .setBlackFadeSource()
                        .build())
                //TODO  .setMusicSource()
                .setMusicCutSource(new MultiMusicCutSource(new OldMusicCutSource(oldMusicCutFile), source))
                .setSpeedAreaSource(new SimpleSpeedAreaSource(source.getBean()))
                .setOutDir(outDir)
                .setMusicOutDelegate(delegate2)
                //TODO  .setIndexDelegate()
                .setTransitionTransfer(new TransitionTransfer())
                .setFilterTransfer(new FilterTransfer())
                .setSpeedEffectTransfer(new SpeedEffectTransfer())
                .setTransitionCutTransfer(new TransitionCutTransfer())
                .build();

        mediator.normalize();
    }

    @Test
    public void testNew(){
        String oldMusicCutFile = "E:\\tmp\\bugfinds\\music_cuts2\\1212\\cuts_uniform.txt";
        String newMusicCutFile = "E:\\tmp\\bugfinds\\新版\\cut.txt";
        String musicDir = "E:\\tmp\\bugfinds\\right_music2";
        String outDir = "E:\\tmp\\bugfinds\\新版\\out";
        MusicOutDelegate2 delegate2 = new DefalutMusicOutDelegate2();

        SimpleMusicCutSource source = new SimpleMusicCutSource(newMusicCutFile);
        MusicCutSource oldMusicCutSrc = new OldMusicCutSource(oldMusicCutFile);
        // TODO
        ExcelSource filterSource =
                new SimpleExcelSource(
                        new ExcelHelper.Builder()
                                .setUseXlsx(true)
                                .setExcelPath("E:\\tmp\\bugfinds\\测试001.xlsx")
                                .setSkipToRowIndex(0)
                                .setSheetName("工作表 1 - 表格 1-1-1-1-1")
                                .build());
        ExcelSource transitionSource = new SimpleExcelSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath("E:\\tmp\\bugfinds\\测试001.xlsx")
                .setSkipToRowIndex(0)
                .setSheetName("工作表 1 - 表格 1-1-1-1")
                .build());
        ExcelSource effectSource = new SimpleExcelSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath("E:\\tmp\\bugfinds\\测试001.xlsx")
                .setSkipToRowIndex(0)
                .setSheetName("工作表 1 - 表格 1-1")
                .build());
        ExcelSource standSource = new SimpleExcelSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath("E:\\tmp\\bugfinds\\测试001.xlsx")
                .setSkipToRowIndex(1)
                .setSheetName("工作表 1 - 表格 2-1")
                .build());
        ExcelSource oldStandSource = new SimpleExcelSource(new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath("E:\\tmp\\bugfinds\\测试001.xlsx")
                .setSkipToRowIndex(0)
                .setSheetName("旧数据1")
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
                .setMusicCutSource(new MultiMusicCutSource(oldMusicCutSrc, source))
                .setSpeedAreaSource(new SimpleSpeedAreaSource(source.getBean()))
                .setMusicSource(new SimpleMusicSource(musicDir))

                .setOutDir(outDir)
                .setMusicOutDelegate(delegate2)
                .setIndexDelegate(new IndexDelegateV3())

                .setTransitionTransfer(new TransitionTransfer())
                .setFilterTransfer(new FilterTransfer())
                .setSpeedEffectTransfer(new SpeedEffectTransfer())
                .setTransitionCutTransfer(new TransitionCutTransfer())
                .build();

        mediator.normalize();
    }
}
