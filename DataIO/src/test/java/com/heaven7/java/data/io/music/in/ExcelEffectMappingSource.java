package com.heaven7.java.data.io.music.in;

import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author heaven7
 */
public class ExcelEffectMappingSource implements EffectMappingSource {

    private static final int INDEX_TYPE = 3;
    private static final int INDEX_DESC = 4;
    private final List<String> effects = new ArrayList<>();
    private final List<String> transitions = new ArrayList<>();

    public ExcelEffectMappingSource(ExcelHelper excelHelper) {
        List<ExcelRow> rows = excelHelper.read();
        VisitServices.from(rows).map(new ResultVisitor<ExcelRow, List<String>>() {
            @Override
            public List<String> visit(ExcelRow row, Object param) {
                List<ExcelCol> columns = row.getColumns();
                String type = columns.get(INDEX_TYPE).getColumnString().trim();
                String desc = columns.get(INDEX_DESC).getColumnString().trim();
                if(!type.isEmpty() && !desc.isEmpty() ){
                    return Arrays.asList(type, desc);
                }
                return null;
            }
        }).fire(new FireVisitor<List<String>>() {
            @Override
            public Boolean visit(List<String> list, Object param) {
                String type = list.get(0);
                if(type.equals("特效")){
                    effects.add(list.get(1));
                }else if(type.equals("转场")){
                    transitions.add(list.get(1));
                }else {
                    System.err.println("wrong type = " + type);
                }
                return null;
            }
        });
        if(!effects.contains("none")){
            effects.add("none");
        }
        if(!transitions.contains("none")){
            transitions.add("none");
        }
    }
    @Override
    public List<String> getSpecialEffects() {
        return effects;
    }
    @Override
    public List<String> getTransitions() {
        return transitions;
    }
}
