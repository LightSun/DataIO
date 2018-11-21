package com.heaven7.java.data.io.music;

import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.*;

/**
 * @author heaven7
 */
public class Configs {

    private static final HashMap<String, String> sDomain_map = new HashMap<>();
    private static final HashMap<String, Integer> sMood_map = new HashMap<>();
    private static final HashMap<String, Integer> sRhythm_map = new HashMap<>();

    static {
        sDomain_map.put("运动", "sport");
        sDomain_map.put("旅行", "travel");
        sDomain_map.put("人物", "person");
        sDomain_map.put("聚会", "party");

        sMood_map.put("标准的", 1);
        sMood_map.put("多变的", 2);
        sMood_map.put("单调的", 0);

        sRhythm_map.put("快节奏", 2);
        sRhythm_map.put("中节奏", 1);
        sRhythm_map.put("慢节奏", 0);
    }

    public static int parseRhythm(String str) {
        return sRhythm_map.get(str);
    }

    public static List<String> parseDomain(String str) {
        String[] strs = str.split(";");
        return VisitServices.from(Arrays.asList(strs)).map(new ResultVisitor<String, String>() {
            @Override
            public String visit(String s, Object param) {
                return sDomain_map.get(s);
            }
        }).getAsList();
    }
    public static int parseMood(String str) {
        return sMood_map.get(str);
    }
    public static List<PartOutput> getAllParts(){
        List<PartOutput> out = new ArrayList<>();
        Collection<String> domains = sDomain_map.values();
        Collection<Integer> properties = sMood_map.values();
        for(String domain : domains){
            for (Integer prop : properties){
                out.add(new PartOutput.SimplePartOutput(domain, prop));
            }
        }
        return out;
    }


}
