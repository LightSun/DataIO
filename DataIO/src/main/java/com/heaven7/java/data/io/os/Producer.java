package com.heaven7.java.data.io.os;

/**
 * @author heaven7
 */
public interface Producer<T> {

    boolean open();

    void produce(SourceContext context, Scheduler scheduler, Callback<T> callback);

    void close();

    interface Callback<T>{
        void onStart(SourceContext context);
        void onProduced(SourceContext context,T t);
        void onEnd(SourceContext context);
    }
}
