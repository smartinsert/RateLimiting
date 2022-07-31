package ratelimiters;

import model.RateLimit;
import model.RequestTimestamps;
import utils.CommonUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowLogsRateLimiter implements RateLimiter {
    private final Map<String, RequestTimestamps> userIdToTimestamps;
    private final ReentrantLock reentrantLock;

    public SlidingWindowLogsRateLimiter() {
        userIdToTimestamps = new HashMap<>();
        reentrantLock = new ReentrantLock();
    }

    @Override
    public boolean addUser(String userId, int numberOfRequest, long timeInSeconds) {
        RateLimit rateLimit = new RateLimit(numberOfRequest, timeInSeconds);
        reentrantLock.lock();
        if (userIdToTimestamps.containsKey(userId))
            throw new RuntimeException("user aleady exists !");
        userIdToTimestamps.put(userId, new RequestTimestamps(rateLimit));
        reentrantLock.unlock();
        return true;
    }

    @Override
    public boolean removeUser(String userId) {
        reentrantLock.lock();
        if (!userIdToTimestamps.containsKey(userId))
            throw new RuntimeException("user does not exists !");
        userIdToTimestamps.remove(userId);
        reentrantLock.unlock();
        return true;
    }

    @Override
    public boolean shouldBeRateLimited(String userId) {
        System.out.println("Calling service for " + userId + " at " + CommonUtils.getCurrentTime());
        reentrantLock.lock();
        boolean isAllowed;
        if (!userIdToTimestamps.containsKey(userId))
            throw new RuntimeException("User does not have rate limits set !");
        RequestTimestamps requestTimestamps = userIdToTimestamps.get(userId);
        LocalDateTime currentTimeInSeconds = CommonUtils.getCurrentTime();
        requestTimestamps.evictOlderTimestamps(currentTimeInSeconds);
        requestTimestamps.addTimestamp(currentTimeInSeconds);
        isAllowed = requestTimestamps.isAllowed();
        reentrantLock.unlock();
        return isAllowed;
    }
    
}
