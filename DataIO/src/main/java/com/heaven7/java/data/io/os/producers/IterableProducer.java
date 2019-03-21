package com.heaven7.java.data.io.os.producers;

import com.heaven7.java.base.anno.Nullable;
import com.heaven7.java.data.io.os.Producer;
import com.heaven7.java.data.io.os.Scheduler;
import com.heaven7.java.data.io.os.SourceContext;

import java.util.Iterator;

/**
 * produce it as Iterator.
 * @author heaven7
 */
public class IterableProducer<T> extends BaseProducer<T> implements Producer<T> {

    private final Iterable<T> it;

    public IterableProducer(Iterable<T> it) {
        this.it = it;
    }

    @Override
    protected void produce0(final SourceContext context, @Nullable Scheduler scheduler, final Callback<T> callback) {
        Iterator<T> it = this.it.iterator();
        while (it.hasNext() && !isClosed()){
            scheduleImpl(context, scheduler, it.next(), callback);
        }
    }
}