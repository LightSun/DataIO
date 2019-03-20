package com.heaven7.java.data.io.os.producer;

import com.heaven7.java.base.anno.Nullable;
import com.heaven7.java.data.io.os.Producer;
import com.heaven7.java.data.io.os.Scheduler;
import com.heaven7.java.data.io.os.SourceContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author heaven7
 */
public abstract class BaseProducer<T> implements Producer<T> {

    private final AtomicBoolean closed = new AtomicBoolean(true);

    @Override
    public boolean open() {
        return closed.compareAndSet(true, false);
    }
    public boolean isClosed(){
        return closed.get();
    }
    @Override
    public void close() {
        closed.compareAndSet(false, true);
    }

    @Override
    public void produce(SourceContext context, Scheduler scheduler, Callback<T> callback) {
        callback.onStart(context);
        produce0(context, scheduler, callback);
        callback.onEnd(context);
        close();
    }

    protected void scheduleImpl(final SourceContext context, @Nullable Scheduler scheduler, final T t, final Callback<T> callback){
        if(scheduler != null){
            scheduler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onProduced(context, t);
                }
            });
        }else {
            callback.onProduced(context, t);
        }
    }

    protected abstract void produce0(SourceContext context, Scheduler scheduler, Callback<T> callback);
}
