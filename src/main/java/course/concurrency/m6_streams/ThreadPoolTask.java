package course.concurrency.m6_streams;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTask {

    // Task #1
    public ThreadPoolExecutor getLifoExecutor() {
        return new ThreadPoolExecutor(5, Runtime.getRuntime().availableProcessors() * 3, 5000, TimeUnit.MILLISECONDS, new LastInFirstOutBlockingQueue());
    }

    // Task #2
    public ThreadPoolExecutor getRejectExecutor() {
        return new ThreadPoolExecutor(8, 8, 5000, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), new ThreadPoolExecutor.DiscardPolicy());
    }
}
