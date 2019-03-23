package com.heaven7.java.data.io.os;

import com.heaven7.java.data.io.os.producers.BaseProducer;

public class TaskNode<T> implements Runnable {

    public static final TaskNode<?> EMPTY = new TaskNode<>(null, null, null, null);

    private final BaseProducer<T> producer;
    private final SourceContext context;
    private final Scheduler scheduler;
    private final Producer.Callback<T> callback;

    public T current;
    public TaskNode nextTask;

    public TaskNode(BaseProducer<T> producer, SourceContext context, Scheduler scheduler, Producer.Callback<T> callback) {
        this.producer = producer;
        this.context = context;
        this.scheduler = scheduler;
        this.callback = callback;
    }

    public void scheduleOrdered() {
        if (current != null) {
            producer.scheduleOrdered(context, scheduler, current, callback, this);
        }
    }

    @Override
    public void run() {
        if (nextTask != null) {
            nextTask.scheduleOrdered();
            nextTask = null;
        }
    }
}