package com.heaven7.java.data.io.music;

import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.data.io.utils.FileUtils;

/**
 * @author heaven7
 */
public class ExcelToJsonUtils {

    //E:\tmp\bugfinds\music2.xlsx E:\tmp\bugfinds\music_cuts\cuts.txt E:\tmp\bugfinds\out 2
    public static void main(String[] args) {
        //$excelpath $cut_config_path $outDir $ship_to_row_index
        if(args.length < 3){
            throw new IllegalArgumentException("format is '$excelpath $cut_config_path $outDir [$ship_to_row_index]'");
        }
        String excelPath = args[0];
        String cutConfigFile = args[1];
        String outDir = args[2];
        String extension = FileUtils.getFileExtension(excelPath);
        String filename = FileUtils.getFileName(excelPath);

        int skipToRowIndex = args.length >=4 ? Integer.valueOf(args[3]) : 2;

        ExcelToJsonAdapter adapter = new ExcelToJsonAdapter(outDir, filename, new SimpleMusicCutProvider(cutConfigFile));
        new ExcelHelper.Builder()
                .setUseXlsx("xlsx".equals(extension))
                .setSheetIndex(0)
                .setExcelPath(excelPath)
                .setSkipToRowIndex(skipToRowIndex)
                .build()
                .readAndWrite(adapter);
    }
}
