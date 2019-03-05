package com.heaven7.java.data.io.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heaven7.java.base.util.ResourceLoader;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author heaven7
 */
public class ContentVerifier {

    @Test
    public void test(){
        final Gson gson = new Gson();
        String dir = "E:\\tmp\\bugfinds\\新版\\out2";
        List<String> files = FileUtils.getFiles(new File(dir), "json", new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return FileUtils.getFileName(pathname.getAbsolutePath()).startsWith("filter_");
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
}
