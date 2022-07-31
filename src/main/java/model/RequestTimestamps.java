package model;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class RequestTimestamps {
    private final Queue<LocalDateTime> timestamps;
    private final ReentrantLock reentrantLock;
    private final RateLimit rateLimit;

    public RequestTimestamps(RateLimit rateLimit) {
        this.timestamps = new LinkedList<>();
        this.reentrantLock = new ReentrantLock();
        this.rateLimit = rateLimit;
    }

    public void evictOlderTimestamps(LocalDateTime currentTime) {
        reentrantLock.lock();
        while (!timestamps.isEmpty()
                && timestamps.peek().isBefore(currentTime.minusSeconds(rateLimit.getTimeInSeconds()))) {
            timestamps.poll();
        }
        reentrantLock.unlock();
    }

    public ReentrantLock getLock() {
        return reentrantLock;
    }

    public void addTimestamp(LocalDateTime currentTime) {
        timestamps.add(currentTime);
    }

    public boolean isAllowed() {
        return timestamps.size() <= rateLimit.getNumberOfRequests();
    }
}
