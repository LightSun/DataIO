package com.heaven7.java.data.io.utils;

import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * @author heaven7
 */
public class FileMd5Helper {

    public static String getMD5Three(String path) {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[8192];
            int len;
            MessageDigest md = MessageDigest.getInstance("MD5");
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
        return bi.toString(16);
    }

    public static void main(String[] args) {
       /* String dir = "D:\\Users\\Administrator\\AppData\\Local\\Temp\\media_files\\resource";
        List<String> files = FileUtils.getFiles(new File(dir), "mp4");
        String md5s = VisitServices.from(files).map(new ResultVisitor<String, String>() {
            @Override
            public String visit(String s, Object param) {
                return getMD5Three(s);
            }
        }).asListService().joinToString(",");
        System.out.println(md5s);*/

       final String dir = "E:\\tmp\\bugfinds\\right_musics\\60s";
       String[] names = {
           "281_short3_homes_0060.mp3",
           "355_short3_mohanam_0059.mp3",
       };
       VisitServices.from(Arrays.asList(names)).fire(new FireVisitor<String>() {
           @Override
           public Boolean visit(String s, Object param) {
               String file = dir + File.separator + s;
               System.out.println(s + " : " + getMD5Three(file));
               return null;
           }
       });
    }
}
