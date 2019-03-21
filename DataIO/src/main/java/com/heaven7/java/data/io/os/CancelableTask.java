package com.heaven7.java.data.io.os;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author heaven7
 */
public final class CancelableTask{

    public static final CancelableTask CANCELLED = new CancelableTask(null, null);

    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final Runnable task;
    private final Callback callback;

    static {
        CANCELLED.cancel();
    }

    private CancelableTask(){
        this(null, null);
    }
    private CancelableTask(Runnable task, Callback callback) {
        this.task = task;
        this.callback = callback;
    }

    public static CancelableTask of(Runnable task, Callback callback){
        return new CancelableTask(task, callback);
    }

    public boolean isCancelled(){
        return cancelled.get();
    }
    public boolean cancel(){
        return cancelled.compareAndSet(false, true);
    }

    public Runnable getBaseTask() {
        return task;
    }

    public Runnable toActuallyTask() {
        callback.onTaskPlan(this);
        return new Runnable() {
            @Override
            public void run() {
                callback.onTaskBegin(CancelableTask.this);
                boolean cancelled = isCancelled();
                if(!cancelled){
                    task.run();
                }
                callback.onTaskEnd(CancelableTask.this, cancelled);
            }
        };
    }

    public interface Callback{
        void onTaskPlan(CancelableTask wrapTask);
        void onTaskBegin(CancelableTask wrapTask);
        void onTaskEnd(CancelableTask wrapTask, boolean cancelled);
    }
}
