package com.heaven7.java.data.io.os.producers;

import com.heaven7.java.base.anno.Nullable;
import com.heaven7.java.data.io.os.CancelableTask;
import com.heaven7.java.data.io.os.Producer;
import com.heaven7.java.data.io.os.Scheduler;
import com.heaven7.java.data.io.os.SourceContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author heaven7
 */
public abstract class BaseProducer<T> implements Producer<T>, CancelableTask.Callback {

    private final AtomicBoolean mClosed = new AtomicBoolean(true);
    private final Set<CancelableTask> mTasks = Collections.synchronizedSet(new HashSet<CancelableTask>());

    @Override
    public boolean open() {
        return mClosed.compareAndSet(true, false);
    }
    public boolean isClosed(){
        return mClosed.get();
    }
    @Override
    public void close() {
        if(mClosed.compareAndSet(false, true)){
            for (CancelableTask task : mTasks){
                task.cancel();
            }
            mTasks.clear();
        }
    }

    @Override
    public final void produce(final SourceContext context, final Scheduler scheduler, final Callback<T> callback) {
        if(scheduler != null){
            post(scheduler, new Runnable() {
                @Override
                public void run() {
                    callback.onStart(context);
                    produce0(context, scheduler, callback);
                    endImpl(context, scheduler, callback);
                }
            });
        }else {
            callback.onStart(context);
            produce0(context, scheduler, callback);
            endImpl(context, scheduler, callback);
        }
    }

    private void endImpl(final SourceContext context, Scheduler scheduler,final Callback<T> callback) {
        if(scheduler == null) {
            close();
            callback.onEnd(context);
        }else {
            post(scheduler, new Runnable() {
                @Override
                public void run() {
                    close();
                    callback.onEnd(context);
                }
            });
        }
    }

    protected CancelableTask scheduleImpl(final SourceContext context, @Nullable Scheduler scheduler, final T t, final Callback<T> callback){
        if(scheduler != null){
            return post(scheduler, new Runnable() {
                @Override
                public void run() {
                    callback.onProduced(context, t);
                }
            });
        }else {
            callback.onProduced(context, t);
        }
        return CancelableTask.CANCELLED;
    }

    protected CancelableTask post(Scheduler scheduler, Runnable task){
        CancelableTask cancelableTask = CancelableTask.of(task, this);
        scheduler.post(cancelableTask.toActuallyTask());
        return cancelableTask;
    }
    protected CancelableTask postDelay(Scheduler scheduler, long delay, Runnable task){
        CancelableTask cancelableTask = CancelableTask.of(task, this);
        scheduler.postDelay(delay, cancelableTask.toActuallyTask());
        return cancelableTask;
    }

    @Override
    public void onTaskPlan(CancelableTask wrapTask) {
         mTasks.add(wrapTask);
    }

    @Override
    public void onTaskBegin(CancelableTask wrapTask) {

    }

    @Override
    public void onTaskEnd(CancelableTask wrapTask, boolean cancelled) {
        mTasks.remove(wrapTask);
    }

    /**
     * call this to produce all products really.
     * @param context the source context
     * @param scheduler the scheduler. can be null
     * @param callback the callback
     */
    protected abstract void produce0(SourceContext context, Scheduler scheduler, Callback<T> callback);
}
