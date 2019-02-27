package com.heaven7.java.data.io.music.in;

import com.google.gson.Gson;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.base.util.ResourceLoader;
import com.heaven7.java.data.io.bean.CutConfigBeanV10;
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
public class SimpleMusicCutSource implements MusicCutSource {

    private final String cutFilePath;
    private final HashMap<MusicItem2,List<CutInfo>> cache = new HashMap<>();
    private CutConfigBeanV10 bean;

    public SimpleMusicCutSource(String cutFilePath) {
        this.cutFilePath = cutFilePath;
    }

    @Override
    public List<CutInfo> getCutInfos(MusicItem2 mi) {
        if(bean == null){
            String json = ResourceLoader.getDefault().loadFileAsString(null, cutFilePath);
            bean = new Gson().fromJson(json, CutConfigBeanV10.class);
        }
        CutConfigBeanV10.CutItem cutItem = bean.getCutItem(mi.getName(), mi.getDuration());
        if(cutItem == null || Predicates.isEmpty(cutItem.getCutLines())){
            return null;
        }
        CutInfo sparseInfo = new CutInfo();
        sparseInfo.setType(CutInfo.TYPE_SPARSE);
        CutInfo intensiveInfo = new CutInfo();
        sparseInfo.setType(CutInfo.TYPE_INTENSIVE);

        List<Float> sparseCuts = VisitServices.from(cutItem.getCutLines()).map(new ResultVisitor<CutConfigBeanV10.CutLine, Float>() {
            @Override
            public Float visit(CutConfigBeanV10.CutLine cutLine, Object param) {
                if (!cutLine.hasFlag(CutInfo.TYPE_SPARSE)) {
                    return null;
                }
                return cutLine.getCut();
            }
        }).getAsList();
        sparseInfo.setCuts(sparseCuts);
        List<Float> intensive = VisitServices.from(cutItem.getCutLines()).map(new ResultVisitor<CutConfigBeanV10.CutLine, Float>() {
            @Override
            public Float visit(CutConfigBeanV10.CutLine cutLine, Object param) {
                return cutLine.getCut();
            }
        }).getAsList();
        intensiveInfo.setCuts(intensive);

        List<CutInfo> list = new ArrayList<>(Arrays.asList(sparseInfo, intensiveInfo));
        cache.put(mi, list);
        return list;
    }
}
