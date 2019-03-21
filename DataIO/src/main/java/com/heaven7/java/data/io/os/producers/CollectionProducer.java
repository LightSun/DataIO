package com.heaven7.java.data.io.os.producers;

import com.heaven7.java.data.io.os.Producer;
import com.heaven7.java.data.io.os.Scheduler;
import com.heaven7.java.data.io.os.SourceContext;

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
    protected void produce0(SourceContext context, Scheduler scheduler, Callback<T> callback) {
        if(collection instanceof List){
            List<T> list = (List<T>) this.collection;
            for(int i = 0, size = list.size() ; i < size ; i ++){
                scheduleImpl(context, scheduler, list.get(i), callback);
                if(isClosed()){
                    break;
                }
            }
        }else {
            for (T t : collection){
                scheduleImpl(context, scheduler, t, callback);
                if(isClosed()){
                    break;
                }
            }
        }
    }

}
