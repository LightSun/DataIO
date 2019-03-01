package com.heaven7.java.data.io.test;

import com.heaven7.java.data.io.bean.EffectOutItem;
import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.music.adapter.IndexDelegateV3;
import com.heaven7.java.data.io.music.in.SimpleExcelSource;
import com.heaven7.java.data.io.music.in.SimpleMusicCutSource;
import com.heaven7.java.data.io.music.in.SimpleSpeedAreaSource;
import com.heaven7.java.data.io.music.transfer.*;
import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.visitor.ResultIndexedVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import org.junit.Test;

import java.util.List;

/**
 * @author heaven7
 */
public class ExcelSourcesTest {

    @Test
    public void testStandSource(){
        String musicCutFile = "E:\\tmp\\bugfinds\\music_cut3\\cut.txt";
        String outDir = "E:\\tmp\\bugfinds\\新版\\temp";

        String exl = "E:\\tmp\\bugfinds\\方案.xlsx";
        ExcelHelper helper = new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(exl)
                .setSheetName("切换音乐 - 表格 1")
                .setSkipToRowIndex(1)
                .build();
        SimpleExcelSource source = new SimpleExcelSource(helper);
        SimpleMusicCutSource cutSource = new SimpleMusicCutSource(musicCutFile);
        OldStandExcelSourceTransfer transfer = new OldStandExcelSourceTransfer(cutSource, new SimpleSpeedAreaSource(cutSource.getBean()),
                new IndexDelegateV3(), outDir);
        List<MusicItem2> items = transfer.transfer(source);
        System.out.println();
    }

    @Test
    public void testTransitionCutSource(){
        String exl = "E:\\tmp\\bugfinds\\方案.xlsx";
        ExcelHelper helper = new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(exl)
                .setSheetName("工作表 1 - 表格 2-1")
                .setSkipToRowIndex(1)
                .build();
        SimpleExcelSource source = new SimpleExcelSource(helper);
        TransitionCutTransfer transfer = new TransitionCutTransfer(new TransitionCutTransfer.Indexer());
        String[] names = {
                "12_four-leaf-clover_0068",
                "12_short1_four-leaf-clover_0022",
        };
        int[] durations = { 15, 15};
        List<MusicItem2> toItems = createMusicItems(names, durations);
        transfer.transfer(source, toItems);
        System.out.println();
    }

    @Test
    public void testFilterSource(){
        String exl = "E:\\tmp\\bugfinds\\方案.xlsx";
        ExcelHelper helper = new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(exl)
                .setSheetName("切换音乐 - 表格 1-1-1-1-1")
                .setSkipToRowIndex(1)
                .build();
        SimpleExcelSource source = new SimpleExcelSource(helper);
        FilterTransfer transfer = new FilterTransfer(new FilterTransfer.Indexer());
        String[] names = {
                "12_four-leaf-clover_0068", "12_four-leaf-clover_0068",
                "12_short1_four-leaf-clover_0022",  "12_short1_four-leaf-clover_0022"
        };
        int[] durations = { 60, 15, 60, 15};
        List<MusicItem2> toItems = createMusicItems(names, durations);
        transfer.transfer(source, toItems);
        System.out.println();
    }

    @Test
    public void testBlackFadeSource(){
        String exl = "E:\\tmp\\bugfinds\\方案.xlsx";
        ExcelHelper helper = new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(exl)
                .setSheetName("切换音乐 - 表格 1-1-1-1")
                .setSkipToRowIndex(2)
                .build();
        SimpleExcelSource source = new SimpleExcelSource(helper);
        TransitionTransfer transfer = new TransitionTransfer(new TransitionTransfer.Indexer());
        String[] names = {
                "12_four-leaf-clover_0068", "12_short1_four-leaf-clover_0022"
        };
        int[] durations = { 60, 15};
        List<MusicItem2> toItems = createMusicItems(names, durations);
        transfer.transfer(source, toItems);
        System.out.println();
        for (MusicItem2 mi : toItems){
            EffectOutItem item = mi.getTransitionItem();
            System.out.println(item);
        }
    }

    @Test
    public void testSpeedEffectSource(){
        String exl = "E:\\tmp\\bugfinds\\方案.xlsx";
        ExcelHelper helper = new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(exl)
                .setSheetName("工作表 1 - 表格 1-1")
                .setSkipToRowIndex(2)
                .build();
        SimpleExcelSource source = new SimpleExcelSource(helper);
        SpeedEffectTransfer transfer = new SpeedEffectTransfer(new SpeedEffectTransfer.Indexer());
        String[] names = {
                "12_four-leaf-clover_0068", "12_short1_four-leaf-clover_0022"
        };
        int[] durations = { 60, 15};
        List<MusicItem2> toItems = createMusicItems(names, durations);
        transfer.transfer(source, toItems);
        System.out.println();
    }

    private static List<MusicItem2> createMusicItems(String[] names, final int[] durations) {
        return VisitServices.from(names).mapIndexed(null, new ResultIndexedVisitor<String, MusicItem2>() {
            @Override
            public MusicItem2 visit(Object param, String s, int index, int size) {
                s = UniformNameHelper.uniformSimpleMusicName(s);
                MusicItem2 mi = new MusicItem2();
                mi.setName(s);
                mi.setId(s);
                mi.setDuration(durations[index]);
                return mi;
            }
        }).getAsList();
    }
}
