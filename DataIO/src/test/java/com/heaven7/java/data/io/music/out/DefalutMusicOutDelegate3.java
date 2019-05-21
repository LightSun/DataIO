package com.heaven7.java.data.io.music.out;

import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.music.in.ExcelSource;
import com.heaven7.java.data.io.music.in.MusicNameSource;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DefalutMusicOutDelegate3 extends DefalutMusicOutDelegate2 {

    private int duration = -1;

    public DefalutMusicOutDelegate3(ExcelSource mServerSource, MusicNameSource mSortSource) {
        super(mServerSource, mSortSource);
    }
    public void setFilterDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public List<MusicItem2> filterMusicItems(List<MusicItem2> beans) {
        if(duration < 0){
            return super.filterMusicItems(beans);
        }
        return VisitServices.from(beans).filter(new PredicateVisitor<MusicItem2>() {
            @Override
            public Boolean visit(MusicItem2 musicItem2, Object param) {
                return musicItem2.getDuration() == duration;
            }
        }).getAsList();
    }

    @Override
    protected List<ServerPairBean> filterServerPair(List<ServerPairBean> beans) {
        if(duration < 0){
            return super.filterServerPair(beans);
        }else {
            List<MusicItem2> tmp = new ArrayList<>();
            for (ServerPairBean bean : new ArrayList<>(beans)){
                tmp.addAll(bean.items);
                //remove item if need.
                for (MusicItem2 item : tmp){
                    if(item.getDuration() != duration){
                        bean.items.remove(item);
                    }
                }
                tmp.clear();
                //remove pair bean if need
                if(bean.items.isEmpty()){
                    beans.remove(bean);
                }
            }
            return beans;
        }
    }
}
