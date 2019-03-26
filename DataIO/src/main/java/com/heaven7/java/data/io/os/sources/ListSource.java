package com.heaven7.java.data.io.os.sources;

import java.util.ArrayList;
import java.util.List;

/**
 * @author heaven7
 */
public abstract class ListSource<T> {

    public abstract List<T> getList();

    /**
     * make the list source to table. which means the every element will as the first column in table.
     * @return the table source.
     */
    public TableSource<T> toTableSource(){
        List<String> titles = null;
        if(this instanceof TitleSource){
            titles = ((TitleSource) this).getTitles();
        }
        List<List<T>> table = new ArrayList<>();
        List<T> list = getList();
        int rowCount = list.size();

        for (int i = 0; i < rowCount ; i ++){
            List<T> ltr = new ArrayList<>();
            ltr.add(list.get(i));
            table.add(ltr);
        }
        if(titles == null){
            return new DirectTableSource<>(table);
        }
        return new TitleTableSource<>(titles, table);
    }
}
