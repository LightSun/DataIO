package com.heaven7.java.data.io.utils;

import com.google.gson.Gson;
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
        List<Item> list = VisitServices.from(FileUtils.getFiles(new File(dir), "mp3"))
                .map(new ResultVisitor<String, Item>() {
                    @Override
                    public Item visit(String s, Object param) {
                        return new Item(s);
                    }
                }).getAsList();
        String file = "E:\\tmp\\bugfinds\\right_musics\\md5_mapping.txt";
        FileUtils.writeTo(file, new Gson().toJson(list));
    }

    static class Item{
        String musicName;
        String md5;
        public Item(String musicFilename) {
            this.musicName = FileUtils.getFileName(musicFilename);
            this.md5 = getMD5Three(musicFilename);
        }
    }
}
