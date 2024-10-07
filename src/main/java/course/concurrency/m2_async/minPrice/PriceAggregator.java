package course.concurrency.m2_async.minPrice;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.math3.util.Decimal64.NAN;

public class PriceAggregator {

    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        List<CompletableFuture<Double>> completableFutures = shopIds.stream()
                .parallel()
                .map(shopId -> CompletableFuture
                        .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), EXECUTOR)
                        .completeOnTimeout(Double.NEGATIVE_INFINITY, 2900, TimeUnit.MILLISECONDS)
                        .exceptionally(it -> Double.NEGATIVE_INFINITY)
                ).toList();

        CompletableFuture.allOf(completableFutures.toArray(CompletableFuture[]::new)).join();

        return completableFutures.stream()
                .mapToDouble(CompletableFuture::join)
                .filter(Double::isFinite)
                .min()
                .orElse(NAN.doubleValue());
    }
}
