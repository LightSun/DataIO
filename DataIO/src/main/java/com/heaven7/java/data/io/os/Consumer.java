package com.heaven7.java.data.io.os;

/**
 * @author heaven7
 */
public interface Consumer<T> {

    /**
     * called on consumers start
     */
    void onStart();
    /**
     * consumers the object
     * @param obj the object
     */
    void onConsume(T  obj);

    /**
     * called on consumers end.
     */
    void onEnd();
}
