package course.concurrency.m3_shared;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {

    private final static AtomicInteger monitor = new AtomicInteger();
    public static void first()  {

        synchronized (monitor) {
            for (int i = 0; i < 3; i++) {
                while (monitor.get() != 0) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(monitor.incrementAndGet());
                monitor.notifyAll();
            }
        }

    }

    public static void second() {

        synchronized (monitor){
            for (int i = 0; i < 3; i++) {
                while (monitor.get() != 1) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(monitor.incrementAndGet());
                monitor.notifyAll();
            }

        }
    }

    public static void third() {
        synchronized (monitor) {
            for (int i = 0; i < 3; i++) {
                while (monitor.get() != 2) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(monitor.incrementAndGet());
                monitor.notifyAll();
                monitor.set(0);
            }
        }

    }


    public static void main(String[] args) {

        Thread t1 = new Thread(() -> first());
        Thread t2 = new Thread(() -> second());
        Thread t3 = new Thread(() -> third());
        t1.start();
        t2.start();
        t3.start();
    }
}
