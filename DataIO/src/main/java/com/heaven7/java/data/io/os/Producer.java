package com.heaven7.java.data.io.os;

import com.heaven7.java.data.io.os.producers.BaseProducer;

/**
 * @author heaven7
 */
public interface Producer<T> {

    int FLAG_SCHEDULE_ORDERED       = 1;
    int FLAG_SCHEDULE_ORDERED_MULTI = 1 << 1;

    boolean open();

    void produce(SourceContext context, Scheduler scheduler, Callback<T> callback);

    void close();

    void setExceptionHandleStrategy(ExceptionHandleStrategy<T> strategy);

    void addFlags(int flags);
    boolean hasFlags(int flags);
    void deleteFlags(int flags);

    interface Callback<T>{
        /**
         * called on start produce
         * @param context the context
         * @param next the core produce task.
         */
        void onStart(SourceContext context, Runnable next);

        /**
         * called on produced
         * @param context the context
         * @param t the product
         */
        void onProduced(SourceContext context,T t);

        /**
         * called on produce end
         * @param context the context
         */
        void onEnd(SourceContext context);
    }
    class WrappedCallback<T> implements Callback<T>{
        final Callback<T> base;

        public WrappedCallback(Callback<T> base) {
            this.base = base;
        }
        @Override
        public void onStart(SourceContext context,  Runnable next) {
            base.onStart(context, next);
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
    interface ExceptionHandleStrategy<T>{
        void handleException(BaseProducer<T> producer, Params params, RuntimeException e);
    }

    class Params{
        public SourceContext context;
        public Scheduler scheduler;
        public ProductionFlow pf;
        public Callback<?> callback;

        public Params(SourceContext context, Scheduler scheduler, ProductionFlow pf, Callback<?> callback) {
            this.context = context;
            this.scheduler = scheduler;
            this.pf = pf;
            this.callback = callback;
        }
    }
    interface ProductionFlow{
        byte TYPE_START      = 1;
        byte TYPE_END        = 2;
        byte TYPE_DO_PRODUCE = 3;
        byte getType();
        Object getExtra();
    }
}
