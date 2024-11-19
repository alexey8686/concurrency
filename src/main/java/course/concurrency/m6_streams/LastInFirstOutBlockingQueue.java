package course.concurrency.m6_streams;

import java.util.concurrent.LinkedBlockingDeque;

public class LastInFirstOutBlockingQueue<E> extends LinkedBlockingDeque<E> {

    @Override public E take() throws InterruptedException {
        return super.takeLast();
    }
}
