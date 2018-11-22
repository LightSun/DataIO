package com.heaven7.java.data.io.music;

import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.data.io.utils.FileUtils;

/**
 * 一首歌存一个json. 不同领域只存id
 * @author heaven7
 */
public class ExcelToJsonUtils {

    //E:\tmp\bugfinds\music2.xlsx 0 E:\tmp\bugfinds\music_cuts\cuts.txt E:\tmp\bugfinds\out 2
    //E:\tmp\bugfinds\music3.xlsx 0 E:\tmp\bugfinds\music_cuts\vida_demo_1122\cuts.txt E:\tmp\bugfinds\out_music3 39
    public static void main(String[] args) {
        //$excelpath $sheet_name $cut_config_path $outDir $ship_to_row_index
        if(args.length < 3){
            throw new IllegalArgumentException("format is '$excelpath $cut_config_path $outDir [$ship_to_row_index]'");
        }
        String excelPath = args[0];
        String sheet_param = args[1];
        String cutConfigFile = args[2];
        String outDir = args[3];
        String extension = FileUtils.getFileExtension(excelPath);
        String filename = FileUtils.getFileName(excelPath);

        int skipToRowIndex = args.length >=5 ? Integer.valueOf(args[4]) : 2;

        ExcelHelper.Builder builder = new ExcelHelper.Builder()
                .setUseXlsx("xlsx".equals(extension))
                .setExcelPath(excelPath)
                .setSkipToRowIndex(skipToRowIndex);
        try {
            int index = Integer.parseInt(sheet_param);
            builder.setSheetIndex(index);
        }catch (NumberFormatException e){
            builder.setSheetName(sheet_param);
        }

        ExcelToJsonAdapter adapter = new ExcelToJsonAdapter(outDir, filename, new SimpleMusicCutProvider(cutConfigFile));
        builder.build().readAndWrite(adapter);
    }
}
