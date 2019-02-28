package com.heaven7.java.data.io.music.adapter;

import com.heaven7.java.base.util.Platforms;
import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.music.in.ExcelSources;
import com.heaven7.java.data.io.music.in.MusicCutSource;
import com.heaven7.java.data.io.music.in.MusicSource;
import com.heaven7.java.data.io.music.in.SpeedAreaSource;
import com.heaven7.java.data.io.music.out.MusicOutDelegate2;
import com.heaven7.java.data.io.music.transfer.AdditionalTransfer;
import com.heaven7.java.data.io.music.transfer.OldStandExcelSourceTransfer;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author heaven7
 */
public class MusicSourceMediator {

    private ExcelSources excelSources;
    private MusicCutSource musicCutSource;
    private SpeedAreaSource speedAreaSource;
    private MusicSource musicSource;
    private MusicOutDelegate2 musicOutDelegate;
    private String outDir;

    private IndexDelegate indexDelegate;
    //---------------------------------------
    private AdditionalTransfer speedEffectTransfer;
    private AdditionalTransfer blackFadeTransfer;
    private AdditionalTransfer filterTransfer;
    private AdditionalTransfer transitionTransfer;

    protected MusicSourceMediator(MusicSourceMediator.Builder builder) {
        this.excelSources = builder.excelSources;
        this.speedAreaSource = builder.speedAreaSource;
        this.musicCutSource = builder.musicCutSource;
        this.musicSource = builder.musicSource;
        this.musicOutDelegate = builder.musicOutDelegate;
        this.outDir = builder.outDir;
        this.indexDelegate = builder.indexDelegate;
        this.speedEffectTransfer = builder.speedEffectTransfer;
        this.blackFadeTransfer = builder.blackFadeTransfer;
        this.filterTransfer = builder.filterTransfer;
        this.transitionTransfer = builder.transitionCutTransfer;
    }
    public void normalize(){
        //transfer.
        StringBuilder sb_warn = new StringBuilder();
        OldStandExcelSourceTransfer standTransfer = new OldStandExcelSourceTransfer(musicCutSource, speedAreaSource,indexDelegate, outDir);
        List<MusicItem2> items = standTransfer.transfer(excelSources.getOldStandSource());
        sb_warn.append(standTransfer.getSb_warn().toString());
        List<MusicItem2> items2 = standTransfer.transfer(excelSources.getStandSource());
        sb_warn.append(standTransfer.getSb_warn().toString());
        items.addAll(items2);
        //transfer other.
        speedEffectTransfer.transfer(excelSources.getSpeedEffectSource(), items);
        blackFadeTransfer.transfer(excelSources.getBlackFadeSource(), items);
        transitionTransfer.transfer(excelSources.getTransitionSource(), items);
        filterTransfer.transfer(excelSources.getFilterSource(), items);

        List<MusicItem2> noMusicItems = new ArrayList<>();
        items = VisitServices.from(items).filter(null, new PredicateVisitor<MusicItem2>() {
            @Override
            public Boolean visit(MusicItem2 mi, Object param) {
                return musicSource.getMusicPath(mi) != null;
            }
        }, noMusicItems).getAsList();
         // write no musics.
        StringBuilder sb = new StringBuilder();
        for (MusicItem2 mi : noMusicItems){
            sb.append(mi.getName()).append(Platforms.getNewLine());
        }
        String noMusicFile = outDir + File.separator + "no_music_items.txt";
        FileUtils.writeTo(noMusicFile, sb.toString());

        String simpleFileName = "music_v10";
        //part outputs
        musicOutDelegate.writePart(outDir, items);
        //total
        musicOutDelegate.writeTotal(outDir, simpleFileName, items);
        //warn
        musicOutDelegate.writeWarn(outDir, simpleFileName, sb_warn.toString());
        //one music-> one json
        musicOutDelegate.writeItem(outDir, items);
        //copy music to one dir
        musicOutDelegate.copyValidMusics(outDir, items);
    }

    public ExcelSources getExcelSources() {
        return this.excelSources;
    }

    public MusicCutSource getMusicCutSource() {
        return this.musicCutSource;
    }

    public MusicSource getMusicSource() {
        return this.musicSource;
    }

    public MusicOutDelegate2 getMusicOutDelegate() {
        return this.musicOutDelegate;
    }

    public String getOutDir() {
        return this.outDir;
    }

    public IndexDelegate getIndexDelegate() {
        return this.indexDelegate;
    }

    public AdditionalTransfer getSpeedEffectTransfer() {
        return this.speedEffectTransfer;
    }

    public AdditionalTransfer getFilterTransfer() {
        return this.filterTransfer;
    }

    public AdditionalTransfer getTransitionTransfer() {
        return this.transitionTransfer;
    }

    public static class Builder {
        private ExcelSources excelSources;
        private MusicCutSource musicCutSource;
        private MusicSource musicSource;
        private MusicOutDelegate2 musicOutDelegate;
        private String outDir;
        private IndexDelegate indexDelegate;
        //---------------------------------------
        private AdditionalTransfer speedEffectTransfer;
        private AdditionalTransfer blackFadeTransfer;
        private AdditionalTransfer filterTransfer;
        private AdditionalTransfer transitionCutTransfer;
        private SpeedAreaSource speedAreaSource;

        public Builder setExcelSources(ExcelSources excelSources) {
            this.excelSources = excelSources;
            return this;
        }

        public Builder setMusicCutSource(MusicCutSource musicCutSource) {
            this.musicCutSource = musicCutSource;
            return this;
        }

        public Builder setMusicSource(MusicSource musicSource) {
            this.musicSource = musicSource;
            return this;
        }

        public Builder setMusicOutDelegate(MusicOutDelegate2 musicOutDelegate) {
            this.musicOutDelegate = musicOutDelegate;
            return this;
        }

        public Builder setOutDir(String outDir) {
            this.outDir = outDir;
            return this;
        }

        public Builder setIndexDelegate(IndexDelegate indexDelegate) {
            this.indexDelegate = indexDelegate;
            return this;
        }

        public Builder setSpeedEffectTransfer(AdditionalTransfer speedEffectTransfer) {
            this.speedEffectTransfer = speedEffectTransfer;
            return this;
        }

        public Builder setBlackFadeTransfer(AdditionalTransfer blackFadeTransfer) {
            this.blackFadeTransfer = blackFadeTransfer;
            return this;
        }

        public Builder setFilterTransfer(AdditionalTransfer filterTransfer) {
            this.filterTransfer = filterTransfer;
            return this;
        }

        public Builder setTransitionCutTransfer(AdditionalTransfer transitionTransfer) {
            this.transitionCutTransfer = transitionTransfer;
            return this;
        }
        public Builder setSpeedAreaSource(SpeedAreaSource speedAreaSource) {
            this.speedAreaSource = speedAreaSource;
            return this;
        }

        public MusicSourceMediator build() {
            return new MusicSourceMediator(this);
        }
    }
}
