package com.heaven7.java.data.io.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heaven7.java.base.util.Platforms;
import com.heaven7.java.base.util.ResourceLoader;
import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.*;
import com.heaven7.java.visitor.collection.VisitServices;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/** @author heaven7 */
public class ContentVerifier {

    @Test
    public void test() {
        final Gson gson = new Gson();
        String dir = "E:\\tmp\\bugfinds\\新版\\out2";
        List<String> files =
                FileUtils.getFiles(
                        new File(dir),
                        "json",
                        new FileFilter() {
                            @Override
                            public boolean accept(File pathname) {
                                return FileUtils.getFileName(pathname.getAbsolutePath())
                                        .startsWith("filter_");
                            }
                        });
        VisitServices.from(files)
                .fire(
                        new FireVisitor<String>() {
                            @Override
                            public Boolean visit(String s, Object param) {
                                String json = ResourceLoader.getDefault().loadFileAsString(null, s);
                                ArrayList<String> filters =
                                        gson.fromJson(
                                                json,
                                                new TypeToken<ArrayList<String>>() {}.getType());
                                System.out.println("file " + s + " >>> filters : " + filters);
                                return null;
                            }
                        });
    }

    // 100个60s的。99个15s的。找出不同的
    @Test
    public void test2() {
        String dir_60 = "E:\\tmp\\bugfinds\\music_cut3\\60s";
        String dir_15 = "E:\\tmp\\bugfinds\\music_cut3\\15s";
        List<String> files_60 = FileUtils.getFiles(new File(dir_60), "csv");
        List<String> files_15 = FileUtils.getFiles(new File(dir_15), "csv");

        VisitServices.from(files_60).diff(null, files_15, new ResultVisitor<String, String>() {
            @Override
            public String visit(String s, Object param) {
                return UniformNameHelper.uniformMusicFilename(s);
            }
        }, new NormalizeVisitor<String, String, String, Void, Test2>() {
            @Override
            public Test2 visit(String key, String s, String s2, Void aVoid, Object param) {
                //s and s2 may be null.
                if(TextUtils.isEmpty(s) || TextUtils.isEmpty(s2)){
                    return null;
                }
                return new Test2(key, s, s2);
            }
        }, new DiffPredicateVisitor<Test2, String>() {
            @Override
            public Boolean visit(Object param, Test2 test2, String s) {
                return !test2.normalName.equals(UniformNameHelper.uniformMusicFilename(s));
            }
        }, new DiffPredicateVisitor<Test2, String>() {
            @Override
            public Boolean visit(Object param, Test2 test2, String s) {
                return !test2.normalName.equals(UniformNameHelper.uniformMusicFilename(s));
            }
        }, new DiffResultVisitor<Test2, String>() {
            @Override
            public void visit(Object param, List<Test2> normalizeList, List<String> currentNonNormalizeList, List<String> otherNonNormalizeList) {
               // System.out.println(currentNonNormalizeList);
               // System.out.println(otherNonNormalizeList);
                String s_60 = VisitServices.from(currentNonNormalizeList).map(new ResultVisitor<String, String>() {
                    @Override
                    public String visit(String s, Object param) {
                        return UniformNameHelper.uniformMusicFilename(s);
                    }
                }).asListService().joinToString(Platforms.getNewLine());
                String s_15 = VisitServices.from(otherNonNormalizeList).map(new ResultVisitor<String, String>() {
                    @Override
                    public String visit(String s, Object param) {
                        return UniformNameHelper.uniformMusicFilename(s);
                    }
                }).asListService().joinToString(Platforms.getNewLine());
                System.out.println(s_60);
                System.out.println("");
                System.out.println(s_15);
            }
        });
    }
    static class Test2 {
        String normalName;
        String name_60;
        String name_15;
        public Test2(String normalName, String name_60, String name_15) {
            this.normalName = normalName;
            this.name_60 = name_60;
            this.name_15 = name_15;
        }
    }
}
