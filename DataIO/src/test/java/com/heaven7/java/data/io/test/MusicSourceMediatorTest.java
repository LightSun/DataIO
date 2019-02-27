package com.heaven7.java.data.io.test;

import com.heaven7.java.data.io.music.adapter.MusicSourceMediator;
import com.heaven7.java.data.io.music.in.ExcelSources;
import com.heaven7.java.data.io.music.in.MultiMusicCutSource;
import com.heaven7.java.data.io.music.in.OldMusicCutSource;
import com.heaven7.java.data.io.music.in.SimpleMusicCutSource;
import com.heaven7.java.data.io.music.out.MusicOutDelegate2;
import com.heaven7.java.data.io.music.transfer.BlackFadeTransfer;
import com.heaven7.java.data.io.music.transfer.FilterTransfer;
import com.heaven7.java.data.io.music.transfer.SpeedEffectTransfer;
import com.heaven7.java.data.io.music.transfer.TransitionCutTransfer;
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

        //--------------------------------------------
        MusicSourceMediator mediator = new MusicSourceMediator.Builder()
                .setExcelSources(new ExcelSources.Builder()
                       //TODO .setBlackFadeSource()
                        .build())
                //TODO  .setMusicSource()
                .setMusicCutSource(new MultiMusicCutSource(new OldMusicCutSource(oldMusicCutFile), new SimpleMusicCutSource(newMusicCutFile)))
                .setOutDir(outDir)
                .setMusicOutDelegate(delegate2)
                //TODO  .setIndexDelegate()
                .setBlackFadeTransfer(new BlackFadeTransfer())
                .setFilterTransfer(new FilterTransfer())
                .setSpeedEffectTransfer(new SpeedEffectTransfer())
                .setTransitionCutTransfer(new TransitionCutTransfer())
                .build();

        mediator.normalize();
    }
}
