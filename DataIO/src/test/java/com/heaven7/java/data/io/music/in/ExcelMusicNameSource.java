package com.heaven7.java.data.io.music.in;

import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.List;

/**
 * @author heaven7
 */
public class ExcelMusicNameSource implements MusicNameSource {

    private final List<ExcelRow> rows ;
    private List<String> musicNames;

    public ExcelMusicNameSource(ExcelHelper mHelper) {
        this.rows = mHelper.read();
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
                                            if(str.endsWith(".mp3")){
                                                str = str.substring(0, str.length() - 4);
                                            }
                                            if(Predicates.isEmpty(str)){
                                                return null;
                                            }
                                            return UniformNameHelper.uniformSimpleMusicName(str);
                                        }
                                    })
                            .getAsList();
            }
            return musicNames;
    }
}
