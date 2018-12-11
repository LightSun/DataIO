package com.heaven7.java.data.io.music.provider;

import com.google.gson.Gson;
import com.heaven7.java.base.util.ResourceLoader;
import com.heaven7.java.data.io.bean.CutConfigBean;
import com.heaven7.java.data.io.bean.CutConfigBeanV2;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

/**
 * @author heaven7
 */
public class MusicCutProviderV2 implements MusicCutProvider {

    protected final CutConfigBeanV2 mBean;

    public MusicCutProviderV2(String cutConfigFile) {
        String json = ResourceLoader.getDefault().loadFileAsString(null, cutConfigFile);
        mBean = new Gson().fromJson(json, CutConfigBeanV2.class);
    }
    @Override
    public String getCuts(String rowName) {
        throw new UnsupportedOperationException();
    }

}
