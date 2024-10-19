package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> atomicReference = new AtomicReference<>(new Bid(0L, 0L, 0L));

    public boolean propose(Bid bid) {
        Bid currnetBid;
        do {
            currnetBid = atomicReference.get();
            if (bid.getPrice() <= currnetBid.getPrice()) {
                return false;
            }

        } while (!atomicReference.compareAndSet(currnetBid, bid));

        notifier.sendOutdatedMessage(currnetBid);

        return true;
    }

    public Bid getLatestBid() {
        return atomicReference.get();
    }
}
