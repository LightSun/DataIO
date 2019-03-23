package com.heaven7.java.data.io.os.internal;

import com.heaven7.java.data.io.os.Producer;
import com.heaven7.java.data.io.os.Scheduler;
import com.heaven7.java.data.io.os.SourceContext;
import com.heaven7.java.data.io.os.TaskNode;
import com.heaven7.java.data.io.os.producers.BaseProducer;

import java.util.Iterator;
import java.util.List;

/**
 * @author heaven7
 */
public class Utils {

    public static <T> TaskNode<T> generateOrderedTasks(BaseProducer<T> producer, Iterable<T> ita,
                                                       SourceContext context, Scheduler scheduler, Producer.Callback<T> callback){
        final Iterator<T> it = ita.iterator();
        TaskNode<T> head = producer.createTaskNode(context, scheduler, callback);
        TaskNode<T> last = head;
        if(it.hasNext()){
            last.current = it.next();
        }
        while (it.hasNext()){
            TaskNode<T> task = producer.createTaskNode(context, scheduler, callback);
            task.current = it.next();
            last.nextTask = task;
            last = task;
        }
        return head;
    }
    public static <T> TaskNode<T> generateOrderedTasks(BaseProducer<T> producer, List<T> list,
                                                                 SourceContext context, Scheduler scheduler, Producer.Callback<T> callback){
        int size = list.size();
        if(size == 0){
            return producer.createTaskNode(null, null, null);
        }
        //create head
        TaskNode<T> head = producer.createTaskNode(context, scheduler, callback);
        TaskNode<T> last = head;
        last.current = list.get(0);

        for(int i = 1; i < size ; i ++){
            TaskNode<T> task = producer.createTaskNode(context, scheduler, callback);
            task.current = list.get(i);
            last.nextTask = task;
            last = task;
        }
        return head;
    }
}
