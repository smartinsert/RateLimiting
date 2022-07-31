package model;

public class RateLimit {
    private final int numberOfRequests;
    private final long timeInSeconds;

    public RateLimit(int numberOfRequests, long timeInSeconds) {
        this.numberOfRequests = numberOfRequests;
        this.timeInSeconds = timeInSeconds;
    }

    public int getNumberOfRequests() {
        return numberOfRequests;
    }

    public long getTimeInSeconds() {
        return timeInSeconds;
    }
}
