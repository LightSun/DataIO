package com.heaven7.java.data.io.music.out;

import com.heaven7.java.base.util.Platforms;
import com.heaven7.java.data.io.music.in.LogWriter;
import com.heaven7.java.data.io.utils.FileUtils;

import java.io.File;

/**
 * @author heaven7
 */
public class FileLogWriter implements LogWriter {

    private final String outDir ;
    private String transferItemFile;
    private String musicNotExistFile;
    private String musicNameFilterFile;
    private String transferEffectFile;
    private StringBuilder sb_transferItemFile;
    private StringBuilder sb_musiNotExist;
    private StringBuilder sb_musiNameFilter;
    private StringBuilder sb_transferEffect;

    public FileLogWriter(String outDir) {
        this.outDir = outDir;
    }

    @Override
    public void writeTransferItem(String transferName, String log) {
        sb_transferItemFile.append("% ")
                .append( transferName)
                .append(" % >>> " )
                .append(log)
                .append(Platforms.getNewLine());
    }

    @Override
    public void writeTransferEffect(String transferName, String log) {
        sb_transferEffect.append(transferName).append(" >>> ")
                .append(log)
                .append(Platforms.getNewLine());
    }

    @Override
    public void writeMusicFileNotExist(String log) {
        sb_musiNotExist.append(log).append(Platforms.getNewLine());
    }

    @Override
    public void start() {
        transferItemFile = outDir + File.separator + "mapping" + File.separator + "trans_item_log.txt";
        musicNotExistFile = outDir + File.separator + "mapping" + File.separator + "music_not_exist.txt";
        musicNameFilterFile = outDir + File.separator + "mapping" + File.separator + "music_name_filter.txt";
        transferEffectFile = outDir + File.separator + "mapping" + File.separator + "transfer_effect_log.txt";
        sb_transferItemFile = new StringBuilder();
        sb_musiNotExist = new StringBuilder();
        sb_musiNameFilter = new StringBuilder();
        sb_transferEffect = new StringBuilder();
    }

    @Override
    public void end() {
        FileUtils.writeTo(transferItemFile, sb_transferItemFile.toString());
        FileUtils.writeTo(musicNotExistFile, sb_musiNotExist.toString());
        FileUtils.writeTo(musicNameFilterFile, sb_musiNameFilter.toString());
        FileUtils.writeTo(transferEffectFile, sb_transferEffect.toString());
    }

    @Override
    public void writeMusicNameFilter(String log) {
        sb_musiNameFilter.append(log).append(Platforms.getNewLine());
    }

}
