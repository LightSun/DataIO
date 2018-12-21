package com.heaven7.java.data.io.music;

import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author heaven7
 */
public class UniformNameHelper {

    private static final List<String> SUFFIXES = Arrays.asList(
            "preview"
    );
    //the second useless str
    private static final List<String> SECOND_USELESS_STR = Arrays.asList(
            "short3"
    );

    public static String uniformSimpleMusicName(String name){
        if(!name.contains("_")){
            return name;
        }
        List<String> strs = new ArrayList<>(Arrays.asList(name.split("_")));
        strs = trimSuffix(strs);
        strs = trimEndDigital(strs);
        if(strs.size() > 2 ){
            strs = trimSecondStr(strs);
        }
        return VisitServices.from(strs).joinToString("_");
    }

    /** return the uniformed simple music filename */
    public static String uniformMusicFilename(String file){
        String fileName = FileUtils.getFileName(file);
        return uniformSimpleMusicName(fileName);
    }

    private static List<String> trimSecondStr(List<String> strs) {
        String secondStr = strs.get(1);
        if(SECOND_USELESS_STR.contains(secondStr)){
            strs.remove(1);
        }
        return strs;
    }

    private static List<String> trimEndDigital(List<String> strs) {
        String endStr = strs.get(strs.size() - 1);
        try {
            Integer.parseInt(endStr);
            //has number .remove last
            strs.remove(strs.size() - 1);
        }catch (NumberFormatException e){
            return strs;
        }
        return strs;
    }

    private static List<String> trimSuffix(List<String> strs) {
        String endStr = strs.get(strs.size() - 1);
        if(SUFFIXES.contains(endStr)){
            strs.remove(strs.size() - 1);
        }
        return strs;
    }

}