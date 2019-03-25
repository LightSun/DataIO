package com.heaven7.java.data.io.os.consumers;

import com.heaven7.java.base.util.Scheduler;
import com.heaven7.java.data.io.os.Consumer;

/**
 * @author heaven7
 */
public class RedirectConsumer<T> implements Consumer<T> {

    private final Consumer<T> base;
    private final Scheduler.Worker worker;

    public RedirectConsumer(Consumer<T> base, Scheduler.Worker worker) {
        this.base = base;
        this.worker = worker;
    }

    @Override
    public void onStart(final Runnable next) {
        worker.schedule(new Runnable() {
            @Override
            public void run() {
                base.onStart(next);
            }
        });
    }

    @Override
    public void onConsume(final T obj,final Runnable next) {
        worker.schedule(new Runnable() {
            @Override
            public void run() {
                base.onConsume(obj, next);
            }
        });
    }

    @Override
    public void onEnd() {
        worker.schedule(new Runnable() {
            @Override
            public void run() {
                base.onEnd();
            }
        });
    }

}
