package ratelimiters;

public interface RateLimiter {
    boolean addUser(String userId, int numberOfRequest, long timeInSeconds);
    boolean removeUser(String userId);
    boolean shouldBeRateLimited(String userId);
}
