package com.heaven7.java.data.io.music;

import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.data.io.bean.MusicItem;
import com.heaven7.java.visitor.FireMultiVisitor2;
import com.heaven7.java.visitor.PileVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author heaven7
 */
public class ErrorVerifier {

    public static void check(MusicItem item, float maxValue, StringBuilder sb_warn){
        List<List<Float>> all = new ArrayList<>();
        if(all.isEmpty()){
            return;
        }
        addIfNotEmpty(item.getSlow_speed_areas(), all);
        addIfNotEmpty(item.getMiddle_speed_areas(), all);
        addIfNotEmpty(item.getHigh_speed_areas(), all);
        if(all.isEmpty()){
            System.err.println("Name: " + item.getName());
        }
        Collections.sort(all, new Comparator<List<Float>>() {
            @Override
            public int compare(List<Float> o1, List<Float> o2) {
                return Float.compare(o1.get(0), o2.get(0));
            }
        });
        final StringBuilder sb = new StringBuilder();
        VisitServices.from(all).fireMulti2(3, 2,null ,new FireMultiVisitor2<List<Float>>(){
            @Override
            public boolean visit(Object param, int count, int step, List<List<Float>> lists) {
                int size = lists.size();
                if(size >= 2){
                    List<Float> l1 = lists.get(0);
                    List<Float> l2 = lists.get(1);
                    //
                    checkRepeat(l1, sb);
                    checkRepeat(l2, sb);
                    //l1.end != l2.start
                    checkAreaSpace(l1, l2, sb);
                }
                if(size >= 3){
                    List<Float> l2 = lists.get(1);
                    List<Float> l3 = lists.get(2);
                    checkRepeat(l3, sb);
                    checkAreaSpace(l2, l3, sb);
                }
                return false;
            }
        });
        Float total = VisitServices.from(all).map(new ResultVisitor<List<Float>, Float>() {
            @Override
            public Float visit(List<Float> floats, Object param) {
                return floats.get(1) - floats.get(0);
            }
        }).pile(PileVisitor.FLOAT_ADD);
        if(total.floatValue() != maxValue){
            sb.append("  时间总和不对: 请检查是否少加了1段或多段. 期望的 是 ")
                    .append(maxValue)
                    .append(" ,实际上是 ")
                    .append(total).append(NEW_LINE);
        }

        //---------- log --------------
        String content = sb.toString();
        if(!Predicates.isEmpty(content)){
            sb_warn.append(item.getName())
                    .append(" [ ")
                    .append(item.getLineNumber())
                    .append(" ]: ")
                    .append(NEW_LINE)
                    .append(content)
                    .append(NEW_LINE);
        }
    }

    private static void checkAreaSpace(List<Float> l1, List<Float> l2, StringBuilder sb) {
        if(!l1.get(1).equals(l2.get(0))){
            sb.append("  unexpect 找到空隙: between " + l1 + " and " + l2).append(NEW_LINE);
        }
    }

    private static void checkRepeat(List<Float> l1, StringBuilder sb) {
        if(l1.get(0).floatValue() == (l1.get(1))){
            sb.append("  unexpect 找到区间内重复: " + l1).append(NEW_LINE);
        }
    }

    private static <T>void addIfNotEmpty(List<T> src , List<T> dest){
        if(!Predicates.isEmpty(src)){
            dest.addAll(src);
        }
    }

    private static final String NEW_LINE = "\r\n";
}
