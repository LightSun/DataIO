package com.heaven7.java.data.io.os.producers;

import com.heaven7.java.data.io.os.Producer;
import com.heaven7.java.data.io.os.Scheduler;
import com.heaven7.java.data.io.os.SourceContext;

import java.util.Collection;

/**
 * produce it as for each.
 * @author heaven7
 */
public class CollectionProducer<T> extends BaseProducer<T> implements Producer<T> {

    private final Collection<T> collection;

    public CollectionProducer(Collection<T> collection) {
        this.collection = collection;
    }

    @Override
    protected void produce0(SourceContext context, Scheduler scheduler, Callback<T> callback) {
        for (T t : collection){
            scheduleImpl(context, scheduler, t, callback);
            if(isClosed()){
                break;
            }
        }
    }

}
