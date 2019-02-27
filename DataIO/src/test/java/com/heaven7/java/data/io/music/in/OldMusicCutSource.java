package com.heaven7.java.data.io.music.in;

import com.google.gson.Gson;
import com.heaven7.java.base.util.ResourceLoader;
import com.heaven7.java.data.io.bean.CutConfigBeanV2;
import com.heaven7.java.data.io.bean.CutInfo;
import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author heaven7
 */
public class OldMusicCutSource implements MusicCutSource {

    protected final CutConfigBeanV2 mBean;
    private HashMap<String, List<CutInfo>> cache = new HashMap<>();

    public OldMusicCutSource(String cutConfigFile) {
        String json = ResourceLoader.getDefault().loadFileAsString(null, cutConfigFile);
        mBean = new Gson().fromJson(json, CutConfigBeanV2.class);
    }

    @Override
    public List<CutInfo> getCutInfos(MusicItem2 item) {
        String key = item.genUniqueId();
        List<CutInfo> cutInfos = cache.get(key);
        if (cutInfos == null) {
            CutConfigBeanV2.CutItem cutItem = mBean.getCutItem(item.getName(), item.getDuration());
            if (cutItem == null) {
                return null;
            }
            List<Float> cuts =
                    VisitServices.from(cutItem.getCutLines())
                            .map(
                                    new ResultVisitor<CutConfigBeanV2.CutLine, Float>() {
                                        @Override
                                        public Float visit(
                                                CutConfigBeanV2.CutLine cutLine, Object param) {
                                            return cutLine.getCut();
                                        }
                                    })
                            .getAsList();

            CutInfo cutInfo = new CutInfo();
            cutInfo.setType(CutInfo.TYPE_INTENSIVE);
            cutInfo.setCuts(cuts);
            cutInfos = new ArrayList<>(Arrays.asList(cutInfo));
            cache.put(key, cutInfos);
        }
        return cutInfos;
    }
}
