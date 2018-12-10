package com.heaven7.java.data.io.music.scan;

import com.heaven7.java.base.util.Logger;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelDataServiceAdapter;
import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author heaven7
 */
public class FilterMusicCutScanner extends MusicCutScanner {

    private static final String TAG = "FilterMusicCutScanner";
    private final List<Item> mDoneItems = new ArrayList<>();

    public FilterMusicCutScanner(String dir, String configXlsPath, List<String> sheetNames) {
        super(dir);
        prepareItems(configXlsPath, sheetNames);
    }

    private void prepareItems(String configXlsPath, List<String> sheetNames) {
        for (String sheetName : sheetNames){
            new ExcelHelper.Builder()
                    .setUseXlsx(true)
                    .setExcelPath(configXlsPath)
                    .setSkipToRowIndex(1)
                    .setSheetName(sheetName)
                    .build().readAndWrite(new ExcelDataServiceAdapter() {
                        @Override
                        public int insertBatch(List<ExcelRow> t) {
                            for (ExcelRow row : t){
                                Item item = new Item();
                                List<ExcelCol> cols = row.getColumns();
                                item.musicName = cols.get(0).getColumnString();
                                item.state = cols.get(1).getColumnString();
                                if(item.state.equals("DONE")){
                                    item.csvName = FileUtils.getFileName(item.musicName) + ".csv";
                                    mDoneItems.add(item);
                                }
                            }
                            return t.size();
                        }
                    });
        }
    }

    @Override
    protected List<String> filter(List<String> files) {
        List<String> newFiles = new ArrayList<>();
        List<String> result = VisitServices.from(files).filter(null, new PredicateVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                for (Item item : mDoneItems) {
                    if (s.endsWith(item.csvName)) {
                        return true;
                    }
                }
                return false;
            }
        }, newFiles).getAsList();
        Logger.d(TAG, "filter" , "after filter . matches is " + result);
        Logger.d(TAG, "filter" , "after filter . not-matches is " + newFiles);
        return result;
    }

    private static class Item{
        String musicName;
        String state;         //PENDING/DONE
        String csvName;
    }

    public static void main(String[] args) {
        String[] names = {
            "shuhuan","chaoliu","dianying","donggan","gudian",
            "guwu","huankuai","weixin","1117-1","1117-2",
            "1117-3","1117-4","1117-5","1117-6","1117-7",
            "1117-8","1117-9","1120-1","1120-2","1120-3",
            "1120-4","1120-5","1120-6","1120-7",
        };
        String dir = "E:\\tmp\\bugfinds\\music_cuts2";
        String musics_path = "E:\\tmp\\bugfinds\\music_cuts2\\music_list.xlsx";
        String cutsPath = "E:\\tmp\\bugfinds\\music_cuts2\\cuts.txt";
        new FilterMusicCutScanner(dir, musics_path, Arrays.asList(names)).serialize(cutsPath);
    }
}
