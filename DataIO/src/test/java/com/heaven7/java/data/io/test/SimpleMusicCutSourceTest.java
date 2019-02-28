package com.heaven7.java.data.io.test;

import com.heaven7.java.data.io.music.in.SimpleMusicCutSource;
import com.heaven7.java.data.io.music.in.TransferCutSource;
import com.heaven7.java.data.io.music.transfer.TransitionCutTransfer;
import com.heaven7.java.data.io.poi.ExcelRow;
import org.junit.Test;

import java.util.List;

/**
 * @author heaven7
 */
public class SimpleMusicCutSourceTest {

    @Test
    public void testTransferCut(){
        String cutFile = "E:\\tmp\\bugfinds\\新版\\cut.txt";
        SimpleMusicCutSource smcs = new SimpleMusicCutSource(cutFile);
        List<ExcelRow> rows = new TransferCutSource(smcs.getBean()).getRows();
        assert rows.size() == TransitionCutTransfer.PERIOD;
    }

}
