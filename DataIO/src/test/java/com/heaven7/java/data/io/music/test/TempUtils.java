package com.heaven7.java.data.io.music.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heaven7.java.base.util.ResourceLoader;
import com.heaven7.java.data.io.bean.MusicMappingItem;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.ArrayList;

/**
 * @author heaven7
 */
public class TempUtils {

    public static void main(String[] args) {
        String json = ResourceLoader.getDefault().loadFileAsString(null, "E:\\tmp\\bugfinds\\out_music9\\name_id_mapping.txt");
        ArrayList<MusicMappingItem> list = new Gson().fromJson(json, new TypeToken<ArrayList<MusicMappingItem>>(){}.getType());
        String str = VisitServices.from(list).map(new ResultVisitor<MusicMappingItem, String>() {
            @Override
            public String visit(MusicMappingItem musicMappingItem, Object param) {
                return FileUtils.getFileName(musicMappingItem.getFilename());
            }
        }).asListService().joinToString("\r\n");
        FileUtils.writeTo("E:\\tmp\\bugfinds\\filenames.txt", str);
    }
}
