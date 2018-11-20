package com.heaven7.java.data.io.music;

import com.google.gson.Gson;
import com.heaven7.java.base.util.ResourceLoader;
import com.heaven7.java.data.io.bean.CutConfigBean;

/**
 * @author heaven7
 */
public class SimpleMusicCutProvider implements MusicCutProvider {

    private final CutConfigBean mBean;

    public SimpleMusicCutProvider(String cutConfigFile) {
        String json = ResourceLoader.getDefault().loadFileAsString(null, cutConfigFile);
        mBean = new Gson().fromJson(json, CutConfigBean.class);
    }
    @Override
    public String getCuts(String rowName) {
        CutConfigBean.CutItem item = mBean.getCutItem(rowName);
        if(item == null){
            System.err.println("cant find cuts for row name = " + rowName);
            return null;
        }
        return item.getCuts();
    }

}
