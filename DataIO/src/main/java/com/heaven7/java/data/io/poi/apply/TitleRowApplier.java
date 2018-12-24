package com.heaven7.java.data.io.poi.apply;

import com.heaven7.java.data.io.apply.ApplyDelegate;
import com.heaven7.java.data.io.poi.write.PoiContext;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

/**
 * @author heaven7
 */
public class TitleRowApplier implements ApplyDelegate<PoiContext> {

    private final List<String> names;
    private final List<Short> colors;

    public TitleRowApplier(List<String> names, final short color) {
        this(names, VisitServices.from(names).map(new ResultVisitor<String, Short>() {
            @Override
            public Short visit(String s, Object param) {
                return color;
            }
        }).getAsList());
    }
    public TitleRowApplier(List<String> names){
        this(names, null);
    }
    public TitleRowApplier(List<String> names, List<Short> colors) {
        this.names = names;
        this.colors = colors;
        if(colors != null){
            assert names.size() == colors.size();
        }
    }

    @Override
    public void apply(PoiContext context) {
        //work boot and sheet must exist
        Workbook workbook = context.getWorkbook();
        Row row = context.getSheet().createRow(0);
        for (int i = 0 , size = names.size() ; i < size ; i ++){
            Cell cell = row.createCell(i, CellType.STRING);
            cell.setCellValue(names.get(i));

            CellStyle style = workbook.createCellStyle();
            Font font = createFont(workbook, i);
            if(colors != null){
                font.setColor(colors.get(i));
            }
            style.setFont(font);
            cell.setCellStyle(style);
        }
    }

    protected Font createFont(Workbook workbook, int index){
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 30);
        return font;
    }
}
