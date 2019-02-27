package com.heaven7.java.data.io.music.helper;

import com.heaven7.java.base.util.Logger;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.data.io.bean.CutInfo;
import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.bean.TimeArea;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.Arrays;
import java.util.List;

/**
 * @author heaven7
 */
public class ParseHelper {

    private static final String TAG = "ParseHelper";

    public static List<List<Float>> parseTimeAreas(final CutInfo source, final MusicItem2 item, String str, final List<TimeArea> tas) {
        if (str == null || str.length() == 0) {
            return null;
        }
        String[] strs = str.split(";");
        try {
            return VisitServices.from(Arrays.asList(strs)).map(new ResultVisitor<String, List<Float>>() {
                @Override
                public List<Float> visit(String s, Object param) {
                    TimeArea timeArea = parseTimeArea(source, item, s);
                    if(timeArea != null) {
                        tas.add(timeArea);
                        return timeArea.asList();
                    }
                    return null;
                }
            }).getAsList();
        }catch (RuntimeException e){
            e.printStackTrace();
            return null;
        }
    }

    private static TimeArea parseTimeArea(CutInfo source, final MusicItem2 item, String str) {
        if (!str.contains("-")) {
            throw new IllegalStateException(str);
        }
        TimeArea ta = new TimeArea();
        try {
            String[] ss = str.split("-");
            if(ss.length != 2){
                Logger.d(TAG, "parseTimeArea", "wrong string: " + str);
                return null;
            }
            float start = parseFloatTime(ss[0]);
            float end = parseFloatTime(ss[1]);
            ta.setBegin(findNearest(source, start));
            ta.setEnd(findNearest(source, end));
            ta.setRawBegin(ss[0]);
            ta.setRawEnd(ss[1]);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return ta;
    }

    private static float findNearest(CutInfo source, float src) {
        float delta = Float.MAX_VALUE;
        float nearest = src;
        for (Float val : source.getCuts()) {
            float del = Math.abs(val - src);
            if (del < delta) {
                delta = del;
                nearest = val;
            }
        }
        return nearest;
    }

    private static float parseFloatTime(String s) {
        if (s.contains(":")) {
            String[] ss = s.split(":");
            int minute = parseDigital(ss[0]);
            float second = parseSecond(ss[1]);
            return minute * 60 + second;
        } else {
            return parseSecond(s);
        }
    }

    private static float parseSecond(String s) {
        while (s.startsWith("0")) {
            s = s.substring(1);
        }
        if (s.length() == 0) {
            return 0;
        }
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            throw new RuntimeException(s, e);
        }
    }

    private static int parseDigital(String s) {
        while (s.startsWith("0")) {
            s = s.substring(1);
        }
        if (s.length() == 0) {
            return 0;
        }
        return Integer.parseInt(s);
    }
}
