package course.concurrency.m2_async.cuncurrentmap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentMapTest {

    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> map = new ConcurrentHashMap<>();

        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 3);
        map.put("4", 4);

        var thread1 = new Thread(() -> map.merge("1", 2, (x, y) -> {
            map.compute("2", (a, b) -> x * y);
            return x * y;
        }));

        var thread2 = new Thread(() -> {
            map.compute("2", (x, y) -> {
                return map.merge("1", 2, (a, b) -> y * b);
            });
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

    }
}
