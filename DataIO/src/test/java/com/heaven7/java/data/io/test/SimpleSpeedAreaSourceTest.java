package com.heaven7.java.data.io.test;

import com.heaven7.java.data.io.bean.CutConfigBeanV10;
import com.heaven7.java.data.io.bean.CutInfo;
import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.music.in.SimpleMusicCutSource;
import com.heaven7.java.data.io.music.in.SimpleSpeedAreaSource;
import org.junit.Test;

import java.util.List;

/**
 * @author heaven7
 */
public class SimpleSpeedAreaSourceTest {

    @Test
    public void test1(){
        String cutFile = "E:\\tmp\\bugfinds\\新版\\cut.txt";
        MusicItem2 mi = new MusicItem2();
        mi.setName(UniformNameHelper.uniformSimpleMusicName("191_attack_of_the_dubstep_clones"));
        mi.setDuration(15);

        SimpleSpeedAreaSource ssas = new SimpleSpeedAreaSource(new SimpleMusicCutSource(cutFile).getBean());
        List<List<Float>> area = ssas.getSpeedArea(mi, CutConfigBeanV10.AREA_TYPE_LOW);
    }

}
