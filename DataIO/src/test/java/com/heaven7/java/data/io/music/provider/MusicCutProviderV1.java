package com.heaven7.java.data.io.music.provider;

import com.google.gson.Gson;
import com.heaven7.java.base.util.ResourceLoader;
import com.heaven7.java.data.io.bean.CutConfigBean;

/**
 * @author heaven7
 */
public class MusicCutProviderV1 implements MusicCutProvider{

    private final CutConfigBean mBean;

    public MusicCutProviderV1(String cutConfigFile) {
        String json = ResourceLoader.getDefault().loadFileAsString(null, cutConfigFile);
        mBean = new Gson().fromJson(json, CutConfigBean.class);
    }

    @Override
    public String getCuts(String rowName) {
        CutConfigBean.CutItem cutItem = mBean.getCutItem(rowName);
        if(cutItem == null){
            return null;
        }
        return cutItem.getCuts();
    }
}
