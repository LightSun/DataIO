package com.heaven7.java.data.io.os.consumers;

import com.heaven7.java.data.io.os.Consumer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author heaven7
 */
public class CollectionConsumer<T> implements Consumer<T> {

    private final List<T> products = new ArrayList<>();
    @Override
    public void onStart() {

    }
    @Override
    public void onConsume(T obj) {
        products.add(obj);
    }
    @Override
    public void onEnd() {
        ArrayList<T> list = new ArrayList<>(products);
        products.clear();
        fire(list);
    }
    protected void fire(List<T> products) {

    }
}
