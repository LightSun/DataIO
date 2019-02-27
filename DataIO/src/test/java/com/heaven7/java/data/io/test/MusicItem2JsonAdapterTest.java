package com.heaven7.java.data.io.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heaven7.java.data.io.bean.CutInfo;
import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.bean.jsonAdapter.MusicItem2JsonAdapter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author heaven7
 */
public class MusicItem2JsonAdapterTest {

    @Test
    public void testOut(){
        List<Float> test1 = Arrays.asList(1.2f, 11f, 13f);
        List<List<Float>> test2 = new ArrayList<>();
        test2.add(Arrays.asList(1.1f, 1.2f, 1.3f));
        test2.add(Arrays.asList(2.1f, 2.2f, 2.3f));
        test2.add(Arrays.asList(3.1f, 3.2f, 3.3f));

        MusicItem2 item = new MusicItem2();
        item.setName("sdfsdfdsfdsf");
        item.setId("sdfsdfdsfdsf");
        item.setProperty(1);
        item.setRhythm(2);
        item.setDomains(Arrays.asList("travel", "person"));

        List<Float> test3 = Arrays.asList(1f,2f,3f,4f,5f,6f,7f,8f,9f);
        List<Float> test4 = Arrays.asList(1f,2f,3f);
        List<CutInfo> infos = new ArrayList<>();
        CutInfo info = new CutInfo();
        info.setType(CutInfo.TYPE_INTENSIVE);
        info.setCuts(test3);
        infos.add(info);
        info = new CutInfo();
        info.setType(CutInfo.TYPE_SPARSE);
        info.setCuts(test4);
        infos.add(info);
        item.setCutInfos(infos);

        item.setTransitionCuts(test1);
        item.setSlow_speed_areas(test2);
        item.setMiddle_speed_areas(test2);
        item.setHigh_speed_areas(test2);

        Gson gson = new GsonBuilder().registerTypeAdapter(MusicItem2.class, new MusicItem2JsonAdapter()).create();
        System.out.println(gson.toJson(item));
    }
}
