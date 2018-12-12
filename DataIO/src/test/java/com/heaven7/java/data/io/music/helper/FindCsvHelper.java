package com.heaven7.java.data.io.music.helper;

import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * one music - on csv
 * @author heaven7
 */
public class FindCsvHelper {

    public static void main(String[] args) {
        String dir = "E:\\tmp\\bugfinds\\music_cuts2\\1212\\60s";
        String outDir = "E:\\tmp\\bugfinds\\music_cuts2\\1212";
        List<String> files = FileUtils.getFiles(new File(dir), "mp3");
        final List<String> notExists = new ArrayList<>();
        VisitServices.from(files).fire(new FireVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                String name = s.substring(0, s.lastIndexOf("."));
                File file = new File(name + ".csv");
                if(!file.exists()){
                    System.out.println("csv not exist: " + s);
                    notExists.add(s);
                }
                return null;
            }
        });

        FileUtils.writeTo(new File(outDir, "csv_not_exists.txt"), notExists.toString());
    }
}
