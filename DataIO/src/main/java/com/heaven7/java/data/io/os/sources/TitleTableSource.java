package com.heaven7.java.data.io.os.sources;

import java.util.List;

/**
 * @author heaven7
 */
public class TitleTableSource<T> extends DirectTableSource<T> implements TitleSource{

    private List<String> titles;

    public TitleTableSource(List<String> titles, List<List<T>> list) {
        super(list);
        this.titles = titles;
    }

    @Override
    public List<String> getTitles() {
        return titles;
    }
}
