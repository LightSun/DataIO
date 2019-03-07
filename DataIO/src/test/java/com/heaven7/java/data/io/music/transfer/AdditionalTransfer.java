package com.heaven7.java.data.io.music.transfer;

import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.music.in.EffectMappingSource;
import com.heaven7.java.data.io.music.in.ExcelSource;
import com.heaven7.java.data.io.music.in.LogWriter;

import java.util.List;

/**
 * @author heaven7
 */
public interface AdditionalTransfer {

    void transfer(ExcelSource effect, List<MusicItem2> toItems);

    void setLogWriter(LogWriter writer);
    LogWriter getLogWriter();

    void setEffectMappingSource(EffectMappingSource source);
    EffectMappingSource getEffectMappingSource();
}
