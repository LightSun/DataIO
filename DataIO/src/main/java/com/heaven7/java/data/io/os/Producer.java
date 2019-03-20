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

    class WrappedCallback<T> implements Callback<T>{
        final Callback<T> base;

        public WrappedCallback(Callback<T> base) {
            this.base = base;
        }
        @Override
        public void onStart(SourceContext context) {
            base.onStart(context);
        }
        @Override
        public void onProduced(SourceContext context, T t) {
            base.onProduced(context, t);
        }
        @Override
        public void onEnd(SourceContext context) {
            base.onEnd(context);
        }
    }
}
