package com.heaven7.java.data.io.music.transfer;

import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.music.in.ExcelSource;

import java.util.List;

/**
 * @author heaven7
 */
public interface AdditionalTransfer {

    void transfer(ExcelSource effect, List<MusicItem2> toItems);

}
