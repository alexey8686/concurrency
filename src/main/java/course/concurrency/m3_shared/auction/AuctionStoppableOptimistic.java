package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicMarkableReference<Bid> latestBid = new AtomicMarkableReference<>(new Bid(0L, 0L, 0L), true);

    public boolean propose(Bid bid) {

        Bid currnetBid;
        boolean bidEnabled;

        do {
            currnetBid = latestBid.getReference();
            bidEnabled = latestBid.isMarked();
            if (!bidEnabled || bid.getPrice() <= currnetBid.getPrice()) {
                return false;
            }

        } while (!latestBid.compareAndSet(currnetBid, bid, bidEnabled, bidEnabled));
        notifier.sendOutdatedMessage(currnetBid);

        return true;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        Bid currentBid;
        do {
            currentBid = latestBid.getReference();
        } while (!latestBid.attemptMark(currentBid, false));
        return currentBid;
    }
}
