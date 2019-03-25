package com.heaven7.java.data.io.os.sources;

import com.heaven7.java.base.util.ResourceLoader;
import com.heaven7.java.base.util.TextReadHelper;

import java.util.List;

/**
 * @author heaven7
 */
public class FileLinesListSource<T> implements ListSource<T> {

    private final Object context;
    private final String file;
    private final TextReadHelper.Callback<T> callback;

    public FileLinesListSource(Object context, String file, TextReadHelper.Callback<T> callback) {
        this.context = context;
        this.file = file;
        this.callback = callback;
    }

    @Override
    public List<T> getList() {
        return ResourceLoader.getDefault().readLines(context, file, callback);
    }
}
