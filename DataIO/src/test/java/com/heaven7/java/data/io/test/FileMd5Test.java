package com.heaven7.java.data.io.test;

import com.heaven7.java.data.io.utils.FileMd5Helper;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import org.junit.Test;

/**
 * @author heaven7
 */
public class FileMd5Test {

    @Test
    public void test1(){

        //String file = "E:\\tmp\\bugfinds\\right_music2\\60s\\4_full_yesterday-s-tango_0077_preview.mp3";
        String file = "E:\\tmp\\bugfinds\\right_music2\\60s\\shanghai.mp3";
        System.out.println(FileMd5Helper.getMD5Three(file));
    }

    @Test
    public void test2(){
        final String dir = "D:\\Users\\WeChat Files\\studyheaven7\\FileStorage\\File\\2019-03\\60s_0326";
        String[] files = {
                "4_short3_it-came-upon-the-midnight-clear-jazz_62",
                "40_short3_funk-force_62",
                "61_short3_haydn-st-antoni-chorale-string-quartet_64",
                "167_short3_bach-jesu-joy-of-man-s-desiring-string-quartet_64",
                "230_short3_bach-air-on-the-g-string_64",
        };
        VisitServices.from(files).fire(new FireVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                System.out.println(String.format("file(%s) md5 is %s.", s,
                        FileMd5Helper.getMD5Three(FileUtils.createFilePath(dir, s + ".mp3"))));
                return null;
            }
        });
    }
}
