package ratelimiters;

// This will maintain a
public class FixedWindowCountersRateLimiter implements RateLimiter {

    @Override
    public boolean addUser(String userId, int numberOfRequest, long timeInSeconds) {
        return false;
    }

    @Override
    public boolean removeUser(String userId) {
        return false;
    }

    @Override
    public boolean shouldBeRateLimited(String userId) {
        return false;
    }
}
