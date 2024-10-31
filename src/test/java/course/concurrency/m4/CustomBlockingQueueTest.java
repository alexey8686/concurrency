package course.concurrency.m4;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class CustomBlockingQueueTest {
    @Test
    public void testFIFO() throws InterruptedException {
        final CustomBlockingQueue<String> queue = new CustomBlockingQueue<>(10);
        queue.enqueue("1");
        queue.enqueue("2");

        var fifo = queue.dequeue();

        Assertions.assertEquals("1", fifo);
    }

    @RepeatedTest(10)
    @Test
    public void queueTestWrite() throws InterruptedException {
        final CustomBlockingQueue<String> queue = new CustomBlockingQueue<>(100);
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 200; i++) {
            int finalI = i;
            executorService.submit(() ->
                    {
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            queue.enqueue(String.valueOf(finalI));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }

        countDownLatch.countDown();
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        Assertions.assertEquals(100, queue.size());
    }

    @RepeatedTest(10)
    @Test
    public void queueTestRead() throws InterruptedException {
        final CustomBlockingQueue<String> queue = new CustomBlockingQueue<>(100);
        for (int i = 0; i < 10; i++) {
            queue.enqueue(String.valueOf(i));
        }
        Assertions.assertEquals(10, queue.size());

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 100; i++) {
            executorService.submit(() ->
                    {
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            queue.dequeue();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }

        countDownLatch.countDown();
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        Assertions.assertEquals(0, queue.size());
    }

    @RepeatedTest(100)
    @Test
    public void queueTestReadWrite() throws InterruptedException {
        final CustomBlockingQueue<String> queue = new CustomBlockingQueue<>(100);
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3);
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        for (int i = 0; i < 200; i++) {
            int finalI = i;
            executorService.submit(() ->
                    {
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            queue.enqueue(String.valueOf(finalI));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );

            executorService.submit(() ->
                    {
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            queue.dequeue();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }

        countDownLatch.countDown();
        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);

        Assertions.assertEquals(0, queue.size());
    }
}