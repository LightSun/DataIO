package com.heaven7.java.data.io.music.scan;

import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描音乐的csv成cutx.txt
 * @author heaven7
 */
public abstract class AbstractMusicCutScanner<CutItem> {

    private final String dir;

    public AbstractMusicCutScanner(String dir) {
        this.dir = dir;
    }

    public final void serialize(String targetFilePath){
        List<String> files = new ArrayList<>();
        FileUtils.getFiles(new File(dir), "csv", new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isHidden();
            }
        }, files);
        files = filter(files);
        List<CutItem> items = VisitServices.from(files).map(
                new ResultVisitor<String, CutItem>() {
            @Override
            public CutItem visit(String csvPath, Object param) {
                return readCutItem(csvPath);
            }
        }).getAsList();
        writeCutConfig(targetFilePath, items);
    }

    /**
     * write cut config file by cut items
     * @param targetFilePath the target save file
     * @param items the cut items
     */
    protected abstract void writeCutConfig(String targetFilePath, List<CutItem> items);
    /*{
        CutConfigBean bean = new CutConfigBean();
        bean.setCutItems(items);
        FileUtils.writeTo(targetFilePath, new Gson().toJson(bean));
    }*/

    /**
     * read the cut item from csv.
     * @param csvPath the music info path
     * @return the cut item.
     */
    protected abstract CutItem readCutItem(String csvPath);

    protected List<String> filter(List<String> files) {
        return files;
    }
}
