package com.heaven7.java.data.io.os.producers;

import com.heaven7.java.data.io.os.Producer;
import com.heaven7.java.data.io.os.Scheduler;
import com.heaven7.java.data.io.os.ProductContext;
import com.heaven7.java.data.io.os.TaskNode;
import com.heaven7.java.data.io.os.internal.Utils;

import java.util.Collection;
import java.util.List;

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
    protected void produce0(final ProductContext context, final Scheduler scheduler, final Callback<T> callback) {
        if(collection instanceof List){
            List<T> list = (List<T>) this.collection;
            for(int i = 0, size = list.size() ; i < size ; i ++){
                scheduleImpl(context, scheduler, list.get(i), callback, i == size - 1);
                if(isClosed()){
                    break;
                }
            }
        }else {
            int size = collection.size();
            int count = 0;
            for (T t : collection){
                scheduleImpl(context, scheduler, t, callback, count++ == size - 1);
                if(isClosed()){
                    break;
                }
            }
        }
    }

    @Override @SuppressWarnings("unchecked")
    protected void produceOrdered(ProductContext context, Scheduler scheduler, Callback<T> callback) {
        TaskNode taskNode = collection instanceof List ? Utils.generateOrderedTasks(this, (List)collection, context, scheduler, callback)
                 : Utils.generateOrderedTasks(this, collection, context, scheduler, callback);
        taskNode.scheduleOrdered();
    }
}
