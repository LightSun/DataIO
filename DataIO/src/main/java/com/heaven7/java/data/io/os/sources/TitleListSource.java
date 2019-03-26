package com.heaven7.java.data.io.os.sources;

import java.util.List;

/**
 * @author heaven7
 */
public class TitleListSource<T> implements ListSource<T>, TitleSource{

    private final ListSource<T> base;
    private final List<String> title;

    public TitleListSource(List<String> title, ListSource<T> base) {
        this.title = title;
        this.base = base;
    }
    @Override
    public List<T> getList() {
        return base.getList();
    }

    @Override
    public List<String> getTitles() {
        return title;
    }
}
