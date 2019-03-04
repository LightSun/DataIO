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
    private static final HashMap<String, Integer> sCategories_map = new HashMap<>();
    private static final HashMap<String, String> sCategories_str = new HashMap<>();

    private static final HashMap<String, String> sEffectMap = new HashMap<>();
    private static final HashMap<String, String> sTransitionMap = new HashMap<>();

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

        sCategories_map.put("旅行", 1);
        sCategories_map.put("动感", 2);
        sCategories_map.put("舒缓", 3);
        sCategories_map.put("欢快", 4);
        sCategories_map.put("古典", 5);

        sCategories_str.put("旅行", "Travel");
        sCategories_str.put("动感", "Dynamic");
        sCategories_str.put("舒缓", "Relaxing");
        sCategories_str.put("欢快", "Cheerful");
        sCategories_str.put("古典", "Classic");

        sEffectMap.put("慢到快", "speed_50_to_speed_200");
        sEffectMap.put("慢速到快速", "speed_50_to_speed_200");
        sEffectMap.put("快到慢", "speed_200_to_speed_50");
        sEffectMap.put("快速到慢速", "speed_200_to_speed_50");
        sEffectMap.put("正常到慢速", "speed_100_to_speed_50");
        sEffectMap.put("正常到慢", "speed_100_to_speed_50");
        sEffectMap.put("慢到正常", "speed_50_to_speed_100");
        sEffectMap.put("快到正常", "speed_120_to_speed_100");
        sEffectMap.put("快速到正常", "speed_120_to_speed_100");
        sEffectMap.put("正常到快", "speed_100_to_speed_120");
        sEffectMap.put("正常到快速", "speed_100_to_speed_120");
        sEffectMap.put("极慢到极快", "speed_25_to_speed_1600");
        sEffectMap.put("极快到极慢", "speed_1600_to_speed_25");

        sEffectMap.put("速度x0.5", "speed_50");
        sEffectMap.put("速度0.5", "speed_50");
        sEffectMap.put("x0.5", "speed_50");
        sEffectMap.put("速度x0.8", "speed_80");
        sEffectMap.put("速度x1.2", "speed_120");
        sEffectMap.put("速度x2", "speed_200");
        sEffectMap.put("速度x4", "speed_400");
        sEffectMap.put("速度4x", "speed_400");
        sEffectMap.put("左平移", "left_translation");
        sEffectMap.put("右平移", "right_translation");
        sEffectMap.put("放大", "zoom_in");
        sEffectMap.put("缩小", "zoom_out");
        sEffectMap.put("无", "none");

        sEffectMap.put("慢速x0.8", "speed_80");
        sEffectMap.put("慢速x0.5", "speed_50");
        sEffectMap.put("极慢x0.25", "speed_25");
        sEffectMap.put("JEPG", "jepg");
        sEffectMap.put("左旋转", "rotate_left");
        sEffectMap.put("右旋转", "rotate_right");

        sEffectMap.put("缓慢放大", "zoom_in");
        sEffectMap.put("缓慢缩小", "zoom_out");
        sEffectMap.put("缓慢上移", "move_up_slow");
        sEffectMap.put("缓慢下移", "move_down_slow");
        sEffectMap.put("缓慢左移", "move_left_slow");
        sEffectMap.put("缓慢右移", "move_right_slow");
        sEffectMap.put("变焦", "zoom");
        sEffectMap.put("左靠位", "position_left");
        sEffectMap.put("右靠位", "position_right");

        sTransitionMap.put("无", "none");
        sTransitionMap.put("叠化", "dissolve");
        sTransitionMap.put("闪白", "white_fade");
        sTransitionMap.put("长叠黑", "black_fade_long");
        sTransitionMap.put("短叠黑", "black_fade_short");
        sTransitionMap.put("zoom xy", "zoom_up_right_down_left");
        sTransitionMap.put("zoom x", "zoom_right_left");
        sTransitionMap.put("zoom y", "zoom_up_down");
        sTransitionMap.put("zoom in", "zoom_in");
        sTransitionMap.put("zoom out", "zoom_out");

        sTransitionMap.put("推动左右", "push_left_right");
        sTransitionMap.put("推动右左", "push_right_left");
        sTransitionMap.put("错位左右", "comb_left_right");
        sTransitionMap.put("错位右左", "comb_right_left");

        sTransitionMap.put("左错位", "comb_right_left");
        sTransitionMap.put("右错位", "comb_left_right");
        sTransitionMap.put("右推动", "push_left_right");
        sTransitionMap.put("左推动", "push_right_left");
        sTransitionMap.put("下推动", "push_down");
        sTransitionMap.put("zoom r", "zoom_rotate");
        sTransitionMap.put("zoom l", "zoom_left");
        sTransitionMap.put("右滚筒", "rotate_right");
        sTransitionMap.put("左滚筒", "rotate_left");
    }

    public static String getEffectStr(String key){
        return sEffectMap.get(key);
    }
    public static String getTransitionStr(String key){
        return sTransitionMap.get(key);
    }

    public static String getCategoryEnglish(String category){
        return sCategories_str.get(category);
    }

    public static Integer parseCategory(String category){
        return sCategories_map.get(category);
    }

    public static int parseRhythm(String str) {
        str = str.replace(" ", "");
        try{
            return sRhythm_map.get(str);
        }catch (RuntimeException e){
            throw e;
        }
    }

    public static List<String> parseDomain(String str) {
        str = str.replace(" ", "");
        String[] strs = str.split(";");
        return VisitServices.from(Arrays.asList(strs)).map(new ResultVisitor<String, String>() {
            @Override
            public String visit(String s, Object param) {
                return sDomain_map.get(s);
            }
        }).getAsList();
    }

    public static int parseMood(String str) {
        if(str.endsWith("\t")){
            str = str.substring(0, str.length() - 1);
        }
        str = str.replace(" ", "");
        try {
            return sMood_map.get(str);
        } catch (Exception e) {
            throw new RuntimeException(str, e);
        }
    }

    public static List<PartOutput> getAllParts() {
        List<PartOutput> out = new ArrayList<>();
        Collection<String> domains = sDomain_map.values();
        // Collection<Integer> properties = sMood_map.values();
        Collection<Integer> rhythms = sRhythm_map.values();
        List<Integer> durations = Arrays.asList(10, 60); //10s, 60s
        for (String domain : domains) {
            for (Integer rhythm : rhythms) {
                for (Integer duration : durations) {
                    out.add(new PartOutput.SimplePartOutput(domain, -1, rhythm, duration));
                }
            }
        }
        return out;
    }

    public static List<PartOutput> getDomainParts(){
        List<PartOutput> out = new ArrayList<>();
        for (String domain : sDomain_map.values()) {
            out.add(new PartOutput.SimplePartOutput(domain, -1, -1, -1));
        }
        return out;
    }

    public static List<PartOutput> getPartsOfPropertyRhythm() {
        List<PartOutput> out = new ArrayList<>();
        Collection<Integer> properties = sMood_map.values();
        Collection<Integer> rhythms = sRhythm_map.values();
        for (Integer prop : properties){
            for (Integer rhythm : rhythms){
                out.add(new PartOutput.SimplePartOutput("-", prop, rhythm, -1));
            }
        }
        return out;
    }
    public static List<PartOutput> getPartsOfDomainRhythm() {
        List<PartOutput> out = new ArrayList<>();
        Collection<String> domains = sDomain_map.values();
        Collection<Integer> rhythms = sRhythm_map.values();
        for (String domain : domains) {
            for (Integer rhythm : rhythms) {
                 out.add(new PartOutput.SimplePartOutput(domain, -1, rhythm, -1));
            }
        }
        return out;
    }
}
