package com.heaven7.java.data.io.os.sources;

import com.heaven7.java.data.io.os.*;

/**
 * @author heaven7
 */
public class SimpleProductManager<T, R> implements ProductManager<T, R> {

    private final Producer<T> producer;
    private ProductContext mContext;
    private Scheduler scheduler;
    private Transformer<? super T, R> transformer;


    public SimpleProductManager(Producer<T> producer) {
        this.producer = producer;
    }
    @Override
    public void setSourceContext(ProductContext context) {
        this.mContext = context;
    }
    @Override
    public ProductContext getSourceContext() {
        return mContext;
    }
    @Override
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public void setTransformer(Transformer<? super T, R> transformer) {
        this.transformer = transformer;
    }
    @Override
    public Transformer<? super T, R> getTransformer() {
        return transformer;
    }

    @Override
    public boolean open(final Consumer<? super R> collector) {
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

        final ProductManager<T, R> source;
        final Transformer<? super T, R> transformer;
        final Consumer<? super R> collector;

        public Callback0(ProductManager<T, R> source, Transformer<? super T, R> transformer, Consumer<? super R> collector) {
            this.source = source;
            this.transformer = transformer;
            this.collector = collector;
        }
        @Override
        public void onStart(ProductContext context, Runnable next) {
            if(collector != null){
                collector.onStart();
            }
            next.run();
        }
        @Override
        public void onProduced(ProductContext context, T t) {
            R result = transformer.consume(context, t);
            if(collector != null){
                collector.onConsume(result);
            }
        }
        @Override
        public void onEnd(ProductContext context) {
            if(collector != null){
                collector.onEnd();
            }
        }
    }
}
