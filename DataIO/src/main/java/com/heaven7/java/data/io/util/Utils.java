package com.heaven7.java.data.io.util;

import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelRow;

import java.util.List;

/**
 * @author heaven7
 */
public class Utils {

    //used for merged column
    public static String getColumnForMultiColumn(List<ExcelCol> columns, int requireIndex, int maxMergeUnitCount){
        String str = columns.get(requireIndex).getColumnString().trim();
        if(TextUtils.isEmpty(str)){
            int count = 1;
            do{
                requireIndex--;
                str = columns.get(requireIndex).getColumnString().trim();
                count ++;
            }while (TextUtils.isEmpty(str) && count < maxMergeUnitCount);
        }
        return str;
    }
    //used for merged rows
    public static String getColumnForMultiRow(ExcelRow current,List<ExcelRow> rows,int columnIndex, int maxMergeRowCount){
        String str = current.getColumns().get(columnIndex).getColumnString().trim();
        if(TextUtils.isEmpty(str)){
            int rowIndex = current.getRowIndex();
            int count = 1;
            do{
                rowIndex--;
                str = rows.get(rowIndex).getColumns().get(columnIndex).getColumnString().trim();
                count ++;
            }while (TextUtils.isEmpty(str) && count <= maxMergeRowCount);
        }
        return str;
    }
}
