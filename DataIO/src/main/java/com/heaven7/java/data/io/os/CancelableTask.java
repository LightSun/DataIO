package com.heaven7.java.data.io.os;

import com.heaven7.java.data.io.os.utils.Exceptions;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author heaven7
 */
public final class CancelableTask{

    public static final CancelableTask CANCELLED = new CancelableTask(null, null);

    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final Runnable task;
    private final Callback callback;
    private Producer.Params params;

    public interface Callback{

        void onTaskPlan(CancelableTask wrapTask);
        void onTaskBegin(CancelableTask wrapTask);
        void onTaskEnd(CancelableTask wrapTask, boolean cancelled);
        void onException(CancelableTask wrapTask, RuntimeException e);
    }
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
    public void reset() {
       if(this.params != null){
           this.params = null;
       }
    }

    public void setProduceParams(Producer.Params params) {
        this.params = params;
    }

    public Producer.Params getProduceParams() {
        return params;
    }
    public boolean isCancelled(){
        return cancelled.get();
    }
    public boolean cancel(){
        return cancelled.compareAndSet(false, true);
    }
    public Runnable toActuallyTask() {
        callback.onTaskPlan(this);
        return new InternalTask(this);
    }

    private static class InternalTask implements Runnable{
        final CancelableTask wrapTask;
        public InternalTask(CancelableTask task) {
            this.wrapTask = task;
        }
        @Override
        public void run() {
            Callback callback = wrapTask.callback;
            try {
                callback.onTaskBegin(wrapTask);
                boolean cancelled = wrapTask.isCancelled();
                if(!cancelled){
                    wrapTask.task.run();
                }
                callback.onTaskEnd(wrapTask, cancelled);
            }catch (Throwable e){
               callback.onException(wrapTask, Exceptions.cast(e));
            }
        }
    }

}
