package com.heaven7.java.data.io.os.sources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * table source that means : every line have multi columns.
 * @author heaven7
 */
public abstract class TableSource<T>{

    /**
     * get the table the out as rows. the inner as columns.
     * @return the table.
     */
    public abstract List<List<T>> getTable();

    public final ListSource<T> getListSource(int index){
        String title = null;
        if(this instanceof TitleSource){
            title = ((TitleSource) this).getTitles().get(index);
        }
        if(title == null){
            return new DirectListSource<>(getTable().get(index));
        }
        return new TitleListSource<>(new ArrayList<String>(Arrays.asList(title)),
                new DirectListSource<>(getTable().get(index)));
    }

    public final TableSource<T> transpose(){
        List<List<T>> result = new ArrayList<>();
        List<List<T>> table = getTable();
        int rowCount = table.size();
        int colCount = table.get(0).size();

        for(int i = 0 ; i < colCount ; i ++){
            List<T> list = new ArrayList<>();
            for(int j = 0 ; j < rowCount ; j ++){
                list.add(table.get(j).get(i));
            }
            result.add(list);
        }
        return new DirectTableSource<>(result);
    }
}
