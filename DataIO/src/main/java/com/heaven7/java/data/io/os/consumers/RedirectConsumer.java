package com.heaven7.java.data.io.os.consumers;

import com.heaven7.java.data.io.os.Consumer;
import com.heaven7.java.data.io.os.Scheduler;

/**
 * @author heaven7
 */
public class RedirectConsumer<T> implements Consumer<T> {

    private final Consumer<T> base;
    private final Scheduler scheduler;

    public RedirectConsumer(Consumer<T> base, Scheduler scheduler) {
        this.base = base;
        this.scheduler = scheduler;
    }

    @Override
    public void onStart() {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                base.onStart();
            }
        });
    }

    @Override
    public void onConsume(final T obj) {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                base.onConsume(obj);
            }
        });
    }

    @Override
    public void onEnd() {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                base.onEnd();
            }
        });
    }

}
