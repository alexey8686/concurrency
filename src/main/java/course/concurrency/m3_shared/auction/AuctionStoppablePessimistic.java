package course.concurrency.m3_shared.auction;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private final Notifier notifier;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(0L, 0L, 0L);

    private final Lock lock = new ReentrantLock();

    private volatile boolean auctionEnabled = Boolean.TRUE;

    public boolean propose(Bid bid) {
        lock.lock();
        try {
            if (auctionEnabled && bid.getPrice() > latestBid.getPrice()) {
                notifier.sendOutdatedMessage(latestBid);
                latestBid = bid;
                return true;
            }
        }
        finally {
            lock.unlock();
        }

        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    public Bid stopAuction() {
        lock.lock();
        try {
            auctionEnabled = false;
            return latestBid;
        }
        finally {
            lock.unlock();
        }
    }
}
