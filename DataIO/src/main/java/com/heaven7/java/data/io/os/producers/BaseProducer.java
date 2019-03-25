package com.heaven7.java.data.io.os.producers;

import com.heaven7.java.data.io.os.*;

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
    private int mFlags;

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
    public void addFlags(int flags) {
        this.mFlags |= flags;
    }

    @Override
    public boolean hasFlags(int flags) {
        return (this.mFlags & flags) == flags;
    }

    @Override
    public void deleteFlags(int flags) {
        this.mFlags &= ~flags;
    }

    @Override
    public void setExceptionHandleStrategy(ExceptionHandleStrategy<T> strategy) {
        this.mExceptionStrategy = strategy;
    }

    public TaskNode<T> createTaskNode(ProductContext context, Scheduler scheduler, Callback<T> callback){
         return new TaskNode<T>(this, context, scheduler, callback);
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
    public final void produce(final ProductContext context, final Scheduler scheduler, final Callback<T> callback) {
        final boolean ordered = hasFlags(FLAG_SCHEDULE_ORDERED);
        final Runnable produce = new Runnable() {
            @Override
            public void run() {
                if(ordered){
                    produceOrdered(context, scheduler, callback);
                }else {
                    produce0(context, scheduler, callback);
                }
            }
        };
        post(scheduler, new Runnable() {
            @Override
            public void run() {
                callback.onStart(context, produce);
            }
        }, new Params(context, scheduler, new BaseProductionProcess(ProductionFlow.TYPE_START, null), callback));
    }

    //may not in order
    public final CancelableTask scheduleImpl(final ProductContext context, final Scheduler scheduler, final T t,
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

    //if next == null. means edn
    public final CancelableTask scheduleOrdered(final ProductContext context, final Scheduler scheduler, final T t,
                                                final Callback<T> callback, final Runnable next){
        return post(scheduler, new Runnable() {
            @Override
            public void run() {
                callback.onProduced(context, t);
                //if is closed or end. dispatch end
                if(isClosed() || next == null){
                    if(next != null && next instanceof TaskNode){
                        ((TaskNode) next).reset();
                    }
                    endImpl(context, scheduler, callback);
                }else {
                    next.run();
                }
            }
        }, new Params(context, scheduler, new BaseProductionProcess(ProductionFlow.TYPE_DO_PRODUCE, t), callback));
    }

    public CancelableTask post(Scheduler scheduler, Runnable task, Params params){
        CancelableTask cancelableTask = CancelableTask.of(task, this);
        cancelableTask.setProduceParams(params);
        scheduler.newWorker().schedule(cancelableTask.toActuallyTask());
        return cancelableTask;
    }

    private void endImpl(final ProductContext context, Scheduler scheduler, final Callback<T> callback) {
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
     * call this to produce all products really. no this may not in order
     * @param context the source context
     * @param scheduler the scheduler. can be null
     * @param callback the callback
     */
    protected abstract void produce0(ProductContext context, Scheduler scheduler, Callback<T> callback);

    /**
     * produce the product in ordered
     * @param context the context
     * @param scheduler the scheduler
     * @param callback the callback
     */
    protected void produceOrdered(ProductContext context, Scheduler scheduler, Callback<T> callback) {

    }
}
