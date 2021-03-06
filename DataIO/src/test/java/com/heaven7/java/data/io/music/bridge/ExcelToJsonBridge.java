package com.heaven7.java.data.io.music.bridge;

import com.heaven7.java.data.io.music.out.DefalutMusicOutDelegate;
import com.heaven7.java.data.io.music.out.MusicOutDelegate1;
import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.data.io.utils.FileUtils;

/**
 * @author heaven7
 */
public abstract class ExcelToJsonBridge {

    private final Parameters mParam;
    private MusicOutDelegate1 mMusicOutDelegate;

    public ExcelToJsonBridge(String[] args) {
        this.mParam = new Parameters(args);
    }
    public ExcelToJsonBridge(Parameters mParam){
        this.mParam = mParam;
    }

    public void setMusicOutDelegate(MusicOutDelegate1 outDelegate) {
        this.mMusicOutDelegate = outDelegate;
    }
    public MusicOutDelegate1 getMusicOutDelegate(){
        return mMusicOutDelegate;
    }

    public Parameters getParameters() {
        return mParam;
    }

    public void execute() {
        if(mMusicOutDelegate == null){
            mMusicOutDelegate = new DefalutMusicOutDelegate();
        }
        ExcelHelper.Builder builder = new ExcelHelper.Builder()
                .setUseXlsx("xlsx".equals(mParam.extension))
                .setExcelPath(mParam.excelPath)
                .setSkipToRowIndex(mParam.skipToRowIndex);
        try {
            int index = Integer.parseInt(mParam.sheet_param);
            builder.setSheetIndex(index);
        } catch (NumberFormatException e) {
            builder.setSheetName(mParam.sheet_param);
        }

        launchBridge(builder.build(), mMusicOutDelegate, mParam);

        // ExcelToJsonAdapter adapter = new ExcelToJsonAdapter(outDir, filename, new MusicCutProviderV2(cutConfigFile));
       /* ExcelToJsonAdapter adapter = new ExcelToJsonAdapter(outDir, filename, new SimpleSpeedMusicCutProvider(cutConfigFile));
        adapter.setMusicOutDelegate(new DefalutMusicOutDelegate());
        adapter.setInputMusicDir(inputMusicDir);
        builder.build().readAndWrite(adapter);*/
    }

    protected abstract void launchBridge(ExcelHelper helper, MusicOutDelegate1 delegate, Parameters param);

    //$excelpath $sheet_name $cut_config_path $outDir $input_music_dir $ship_to_row_index
    protected static class Parameters {
        public String excelPath;
        public String sheet_param;
        public String cutConfigFile;
        public String outDir;
        public String inputMusicDir;
        public String extension;
        public String filename;
        public int skipToRowIndex;

        /** used internal */
        protected Parameters(String outDir, String filename) {
            this.outDir = outDir;
            this.filename = filename;
        }
        //$excelpath $sheet_name $cut_config_path $outDir $input_music_dir $ship_to_row_index
        public Parameters(String[] args) {
            excelPath = args[0];
            sheet_param = args[1];
            cutConfigFile = args[2];
            outDir = args[3];
            inputMusicDir = args[4];
            extension = FileUtils.getFileExtension(excelPath);
            filename = FileUtils.getFileName(excelPath);
            skipToRowIndex = args.length >= 6 ? Integer.valueOf(args[5]) : 2;
        }
    }
}
