package com.heaven7.java.data.io.test;

import com.heaven7.java.base.util.threadpool.Executors2;
import com.heaven7.java.data.io.os.Scheduler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Schedulers {
	
	public static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final Scheduler DEFAULT = new DefaultScheduler();
	public static final Scheduler GROUP_ASYNC = new GroupAsyncScheduler();

	public static String getCurrentTime(){
		return DF.format(new Date(System.currentTimeMillis()));
	}
	
    private static class GroupAsyncScheduler implements Scheduler{

		final ScheduledExecutorService pool = Executors2.newScheduledThreadPool(5);

		@Override
		public void postDelay(long delay, final Runnable task) {
			pool.schedule(task, delay, TimeUnit.MILLISECONDS);
		}

		@Override
		public void post(final Runnable task) {
			pool.submit(task);
		}
	}
	private static class DefaultScheduler implements Scheduler{
		@Override
		public void postDelay(long delay, Runnable task) {
			try {
				Thread.sleep(delay);
				task.run();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void post(Runnable task) {
			task.run();
		}
	}
	
}
