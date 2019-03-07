package com.heaven7.java.data.io.utils;

import com.heaven7.java.base.util.Platforms;
import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.data.io.bean.NameDeleagte;
import com.heaven7.java.data.io.music.in.MusicNameSource;
import com.heaven7.java.visitor.*;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.MapVisitService;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.java.visitor.util.Map;

import java.util.List;

/**
 * @author heaven7
 */
public class Debugger {

    public static <T extends NameDeleagte> void debugPair(MusicNameSource source, List<T> list, String logPath,final String mark){
        final StringBuilder sb_name_check = new StringBuilder();
        MapVisitService<String, List<T>> mapService = VisitServices.from(list).groupService(
                new ResultVisitor<T, String>() {
                    @Override
                    public String visit(T cutItem, Object param) {
                        return cutItem.getName();
                    }
                }).filter(new MapPredicateVisitor<String, List<T>>() {
            @Override
            public Boolean visit(KeyValuePair<String, List<T>> pair, Object param) {
                List<T> value = pair.getValue();
                List<Integer> list = VisitServices.from(value).map(new ResultVisitor<T, Integer>() {
                    @Override
                    public Integer visit(T cutItem, Object param) {
                        return cutItem.getDuration();
                    }
                }).getAsList();
                String msg = "";
                if(!list.contains(60)){
                    msg += " doesn't have "+ mark + " for 60s.";
                }
                if(!list.contains(15)){
                    msg += " doesn't have "+ mark + " for 15s.";
                }
                if(!TextUtils.isEmpty(msg)){
                    sb_name_check.append("Music name: ").append(pair.getKey()).append(msg).append(Platforms.getNewLine());
                    return false;
                }
                return true;
            }
        }, null);
        final Map<String, List<T>> map = mapService.get();
        VisitServices.from(source.getMusicNames()).diff(null, map.keys(), Visitors.<String, String>unchangeResultVisitor(),
                new NormalizeVisitor<String, String, String, Void, KeyValuePair<String, String>>() {
                    @Override
                    public KeyValuePair<String, String> visit(String key, String s, String s2, Void aVoid, Object param) {
                        if(s == null || s2 == null){
                            return null;
                        }
                        return KeyValuePair.create(s, s2);
                    }
                }, new DiffPredicateVisitor<KeyValuePair<String, String>, String>() {
                    @Override
                    public Boolean visit(Object param, KeyValuePair<String, String> pair, String s) {
                        return !pair.getValue().equals(s);
                    }
                }, new DiffPredicateVisitor<KeyValuePair<String, String>, String>() {
                    @Override
                    public Boolean visit(Object param, KeyValuePair<String, String> pair, String s) {
                        return !pair.getKey().equals(s);
                    }
                }, new DiffResultVisitor<KeyValuePair<String, String>, String>() {
                    @Override
                    public void visit(Object param, List<KeyValuePair<String, String>> normalizeList, List<String> currentNonNormalizeList, List<String> otherNonNormalizeList) {
                        if(normalizeList.size() != map.size()){
                            sb_name_check.append(String.format("matched count is %d, but pair(60-15) count is %d ", normalizeList.size(), map.size()))
                                    .append(Platforms.getNewLine());
                        }
                        if(!currentNonNormalizeList.isEmpty()){
                            sb_name_check.append("music ").append(mark).append(" not exist,  list(names): ")
                                    .append(currentNonNormalizeList).append(Platforms.getNewLine());
                        }
                        if(!otherNonNormalizeList.isEmpty()){
                            sb_name_check.append("music ").append(mark).append("is redundant, list(names): ")
                                    .append(otherNonNormalizeList).append(Platforms.getNewLine());
                        }
                    }
                });
        FileUtils.writeTo(logPath, sb_name_check.toString());
    }
}
