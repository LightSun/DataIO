package com.heaven7.java.data.io.test;

import com.heaven7.java.data.io.utils.FileMd5Helper;
import org.junit.Test;

/**
 * @author heaven7
 */
public class FileMd5Test {

    @Test
    public void test1(){

        String file = "E:\\tmp\\bugfinds\\right_music2\\60s\\239_short3_exciting-prelude_0069.mp3";
        System.out.println(FileMd5Helper.getMD5Three(file));
    }
}
