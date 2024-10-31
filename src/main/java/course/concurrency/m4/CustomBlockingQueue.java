package course.concurrency.m4;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;


public class CustomBlockingQueue<T> {

    private final Object monitor = new Object();

    private final AtomicInteger size = new AtomicInteger(0);
    private final Queue<T> queue;

    private final Integer queueMaxSize;
    public CustomBlockingQueue(Integer queueMaxSize) {
        this.queue = new ArrayDeque<>(queueMaxSize);
        this.queueMaxSize = queueMaxSize;
    }

    public void enqueue(T value) throws InterruptedException {
        synchronized (monitor){
            while (size.get() >= this.queueMaxSize){
                monitor.wait();
            }
            queue.offer(value);
            System.out.println(size.incrementAndGet());
            monitor.notifyAll();

        }
    }
    public T dequeue() throws InterruptedException {
        synchronized (monitor){
            while (size.get() == 0){
                monitor.wait();
            }
            System.out.println(size.decrementAndGet());
            monitor.notifyAll();
            return queue.poll();
        }
    }

    public int size(){
        return size.get();
    }
}
