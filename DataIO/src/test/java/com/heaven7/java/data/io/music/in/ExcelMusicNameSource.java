package com.heaven7.java.data.io.music.in;

import com.heaven7.java.base.anno.Nullable;
import com.heaven7.java.base.util.Platforms;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author heaven7
 */
public class ExcelMusicNameSource implements MusicNameSource {

    private final List<ExcelRow> rows;
    private List<String> musicNames;
    private Map<String, Integer> sortMap;

    public ExcelMusicNameSource(ExcelHelper standName,@Nullable ExcelHelper sortName) {
        this.rows = standName.read();
        this.sortMap = sortName != null ? readSortNames(sortName) : null;
        if(sortMap != null){
            getMusicNames();
        }
    }

    private Map<String, Integer> readSortNames(ExcelHelper sortName) {
        List<String> list = VisitServices.from(sortName.read()).map(new ResultVisitor<ExcelRow, String>() {
            @Override
            public String visit(ExcelRow excelRow, Object param) {
                List<ExcelCol> columns = excelRow.getColumns();
                String name = columns.get(0).getColumnString();
                return UniformNameHelper.uniformSimpleMusicName(name);
            }
        }).getAsList();
        Map<String, Integer> map = new HashMap<>();
        int len = list.size();
        for (int i = 0; i < len; i++) {
            map.put(list.get(i), i);
        }
        return map;
    }

    @Override
    public List<String> getMusicNames() {
        if (musicNames == null) {
            musicNames =
                    VisitServices.from(rows)
                            .map(
                                    new ResultVisitor<ExcelRow, String>() {
                                        @Override
                                        public String visit(ExcelRow row, Object param) {
                                            // the name of 60s
                                            String str = row.getColumns().get(1).getColumnString();
                                            if (str.endsWith(".mp3")) {
                                                str = str.substring(0, str.length() - 4);
                                            }
                                            if (Predicates.isEmpty(str)) {
                                                return null;
                                            }
                                            return UniformNameHelper.uniformSimpleMusicName(str);
                                        }
                                    })
                            .getAsList();
            //assert music name is correct
            if(sortMap != null){
                final StringBuilder sb_info = new StringBuilder();
                VisitServices.from(musicNames).fire(new FireVisitor<String>() {
                    @Override
                    public Boolean visit(String s, Object param) {
                        if(sortMap.get(s) == null){
                            sb_info.append(s).append(Platforms.getNewLine());
                            //throw new UnsupportedOperationException("music name and sort name is not full mapped. by music name = " + s);
                        }
                        return null;
                    }
                });
                if(sb_info.length() > 0){
                    String file = "G:\\work\\bugfinds\\warn\\sort_warn.txt";
                    FileUtils.writeTo(file, sb_info.toString());
                }
            }
        }
        return musicNames;
    }

    @Override
    public Map<String, Integer> getSortMap() {
        return sortMap;
    }
}
