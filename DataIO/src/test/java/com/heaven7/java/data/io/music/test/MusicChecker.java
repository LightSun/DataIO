package com.heaven7.java.data.io.music.test;

import com.google.gson.Gson;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.utils.FileMd5Helper;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.NormalizeVisitor;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 检查正版音乐 和 当前所在60s音乐是否一致。
 * @author heaven7
 */
public class MusicChecker {

    private final String okDir;
    private final String checkDir;
    private final String outDir;

    public MusicChecker(String okDir, String checkDir, String outDir) {
        this.okDir = okDir;
        this.checkDir = checkDir;
        this.outDir = outDir;
    }

    /*
    92a481a0e0a8aead8e115106d88d213a
    8535aab197b3ffcfe6679ef8fe4d5bf
    4b960176acac55895049d04e6bfdb36c
    62991d0ea2be53850afc0f0ddde3d594
    a74af2bbf57b400c16ab477eb5f420fe
     *
     */
    public static void main(String[] args) {
        String okDir = "E:\\tmp\\bugfinds\\right_musics\\60s音乐to陈军";
        String checkDir = "E:\\tmp\\bugfinds\\right_musics\\60s";
        String outDir = "E:\\tmp\\bugfinds\\right_musics\\check";
        new MusicChecker(okDir, checkDir, outDir).startCheck();
    }

    public void startCheck(){
        List<String> okMp3s = FileUtils.getFiles(new File(okDir), "mp3");
        List<String> checkMp3s = FileUtils.getFiles(new File(checkDir), "mp3");
        List<Item> okList = VisitServices.from(okMp3s).map(new ResultVisitor<String, Item>() {
            @Override
            public Item visit(String s, Object param) {
                return new Item(s);
            }
        }).getAsList();
        List<Item> checkList = VisitServices.from(checkMp3s).map(new ResultVisitor<String, Item>() {
            @Override
            public Item visit(String s, Object param) {
                return new Item(s);
            }
        }).getAsList();
        //normalize
        final List<MatchItem> matchItems = VisitServices.from(okList)
                .normalize(null, checkList, new ResultVisitor<Item, String>() {
            @Override
            public String visit(Item item, Object param) {
                return item.musicName;
            }
        }, new ResultVisitor<Item, String>() {
            @Override
            public String visit(Item item, Object param) {
                return item.musicName;
            }
        }, new NormalizeVisitor<String, Item, Item, Void, MatchItem>() {
            @Override
            public MatchItem visit(String key, Item item, Item item2, Void aVoid, Object param) {
                return new MatchItem(item, item2);
            }
        }).mapValue().getAsList();
        // not matched ok items.
        List<Item> nomatchList_ok = VisitServices.from(okList).filter(new PredicateVisitor<Item>() {
            @Override
            public Boolean visit(Item item, Object param) {
                return !containsOkItem(matchItems, item);
            }
        }).getAsList();
        List<Item> nomatchList_check = VisitServices.from(checkList).filter(new PredicateVisitor<Item>() {
            @Override
            public Boolean visit(Item item, Object param) {
                return !containsCheckItem(matchItems, item);
            }
        }).getAsList();
        //check md5
        List<MatchItem> realMatched = new ArrayList<>();
        List<MatchItem> realNoMatch = VisitServices.from(matchItems)
                .filter(null, new PredicateVisitor<MatchItem>() {
            @Override
            public Boolean visit(MatchItem matchItem, Object param) {
                return !matchItem.isSameMd5();
            }
        }, realMatched).getAsList();

        //start write
        String nomatchList_ok_path = outDir + File.separator + "nomatchList_ok.json";
        String nomatchList_check_path = outDir + File.separator + "nomatchList_check.json";
        String realMatched_path = outDir + File.separator + "realMatched.json";
        String realNoMatch_path = outDir + File.separator + "realNoMatch.json";
        FileUtils.writeTo(nomatchList_ok_path, new Gson().toJson(nomatchList_ok));
        FileUtils.writeTo(nomatchList_check_path, new Gson().toJson(nomatchList_check));
        FileUtils.writeTo(realMatched_path, new Gson().toJson(realMatched));
        FileUtils.writeTo(realNoMatch_path, new Gson().toJson(realNoMatch));
    }

    private static boolean containsOkItem(List<MatchItem> matchItems, Item ok){
        for (MatchItem mi : matchItems){
            if(mi.containOkItem(ok)){
                return true;
            }
        }
        return false;
    }
    private static boolean containsCheckItem(List<MatchItem> matchItems, Item check){
        for (MatchItem mi : matchItems){
            if(mi.containCheckItem(check)){
                return true;
            }
        }
        return false;
    }

    private static class MatchItem{
        final Item ok;
        final Item check;

        public MatchItem(Item ok, Item check) {
            this.ok = ok;
            this.check = check;
        }
        public boolean containOkItem(Item item){
            return ok == item;
        }
        public boolean containCheckItem(Item item){
            return check == item;
        }
        public boolean isSameMd5() {
            return ok.md5.equals(check.md5);
        }
    }

    private static class Item{
        final String filename;
        final String md5;
        final String musicName;

        public Item(String filename) {
            this.filename = filename;
            this.musicName = UniformNameHelper.uniformMusicFilename(filename);
            this.md5 = FileMd5Helper.getMD5Three(filename);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return Objects.equals(musicName, item.musicName);
        }
        @Override
        public int hashCode() {
            return Objects.hash(musicName);
        }
    }
}
