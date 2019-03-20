package com.heaven7.java.data.io.os.sources;

import com.heaven7.java.base.anno.NonNull;
import com.heaven7.java.data.io.os.*;

/**
 * @author heaven7
 */
public class SimpleIndefiniteSource<T, R> implements IndefiniteSource<T, R> {

    private final Producer<T> producer;
    private final Scheduler scheduler;
    private SourceContext mContext;

    public SimpleIndefiniteSource(Producer<T> producer, Scheduler scheduler) {
        this.scheduler = scheduler;
        this.producer = producer;
    }
    @Override
    public void setSourceContext(SourceContext context) {
        this.mContext = context;
    }
    @Override
    public boolean open(final @NonNull Transformer<T, R> transformer, final Consumer<? super R> collector) {
        //already opened.
        if(!producer.open()){
            return false;
        }
        producer.produce(mContext, scheduler, new Callback0<T,R>(this, transformer, collector));
        return true;
    }

    @Override
    public void close() {
        producer.close();
    }

    private static class Callback0<T, R> implements Producer.Callback<T>{

        final IndefiniteSource<T, R> source;
        final Transformer<T, R> transformer;
        final Consumer<? super R> collector;

        public Callback0(IndefiniteSource<T, R> source, Transformer<T, R> transformer, Consumer<? super R> collector) {
            this.source = source;
            this.transformer = transformer;
            this.collector = collector;
        }
        @Override
        public void onStart(SourceContext context) {
            if(collector != null){
                collector.onStart();
            }
        }
        @Override
        public void onProduced(SourceContext context, T t) {
            R result = transformer.consume(context, t);
            if(collector != null){
                collector.onConsume(result);
            }
        }
        @Override
        public void onEnd(SourceContext context) {
            if(collector != null){
                collector.onEnd();
            }
        }
    }
}
