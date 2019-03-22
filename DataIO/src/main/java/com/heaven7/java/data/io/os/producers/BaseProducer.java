package com.heaven7.java.data.io.os.producers;

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
    private ExceptionHandleStrategy<T> mExceptionStrategy;

    protected static class BaseProductionProcess implements ProductionFlow{

        private final byte type;
        private final Object extra;
        public BaseProductionProcess(byte type, Object extra) {
            this.type = type;
            this.extra = extra;
        }
        @Override
        public byte getType() {
            return type;
        }
        @Override
        public Object getExtra() {
            return extra;
        }
    }

    @Override
    public void setExceptionHandleStrategy(ExceptionHandleStrategy<T> strategy) {
        this.mExceptionStrategy = strategy;
    }
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
                task.reset();
            }
            mTasks.clear();
        }
    }

    @Override
    public final void produce(final SourceContext context, final Scheduler scheduler, final Callback<T> callback) {
        post(scheduler, new Runnable() {
            @Override
            public void run() {
                callback.onStart(context);
                produce0(context, scheduler, callback);
            }
        }, new Params(context, scheduler, new BaseProductionProcess(ProductionFlow.TYPE_START, null), callback));
    }

    protected CancelableTask scheduleImpl(final SourceContext context, final Scheduler scheduler, final T t,
                                          final Callback<T> callback, final boolean end){
        return post(scheduler, new Runnable() {
            @Override
            public void run() {
                callback.onProduced(context, t);
                //if is closed or end. dispatch end
                if(isClosed() || end){
                    endImpl(context, scheduler, callback);
                }
            }
        }, new Params(context, scheduler, new BaseProductionProcess(ProductionFlow.TYPE_DO_PRODUCE, t), callback));
    }

    public CancelableTask post(Scheduler scheduler, Runnable task, Params params){
        CancelableTask cancelableTask = CancelableTask.of(task, this);
        cancelableTask.setProduceParams(params);
        scheduler.schedule(cancelableTask.toActuallyTask());
        return cancelableTask;
    }

    private void endImpl(final SourceContext context, Scheduler scheduler, final Callback<T> callback) {
        post(scheduler, new Runnable() {
            @Override
            public void run() {
                close();
                callback.onEnd(context);
            }
        }, new Params(context, scheduler, new BaseProductionProcess(ProductionFlow.TYPE_END, null),
                callback));
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
        wrapTask.reset();
    }
    @Override
    public void onException(CancelableTask wrapTask, RuntimeException e) {
        Params params = wrapTask.getProduceParams();
        mTasks.remove(wrapTask);
        wrapTask.reset();
        if(mExceptionStrategy != null){
            mExceptionStrategy.handleException(this, params, e);
        }else {
            throw e;
        }
    }

    /**
     * call this to produce all products really.
     * @param context the source context
     * @param scheduler the scheduler. can be null
     * @param callback the callback
     */
    protected abstract void produce0(SourceContext context, Scheduler scheduler, Callback<T> callback);

}
