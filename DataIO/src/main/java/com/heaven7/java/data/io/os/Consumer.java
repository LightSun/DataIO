package com.heaven7.java.data.io.os;

/**
 * @author heaven7
 */
public interface Consumer<T> {

    /**
     * called on collector start
     */
    void onStart();
    /**
     * collector the object
     * @param obj the object
     */
    void onConsume(T  obj);

    /**
     * called on collector end.
     */
    void onEnd();
}
