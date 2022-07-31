package providers;

import enums.RateLimiterTypes;
import ratelimiters.FixedWindowCountersRateLimiter;
import ratelimiters.RateLimiter;
import ratelimiters.SlidingWindowLogsRateLimiter;

/*
* Following are the prevalent rate limiting algorithms
* 1. Token Bucket
* 2. Leaky Bucket
* 3. Sliding Window
* 4. Sliding Window Log
* 5. Sliding Window Counter
* */
public class RateLimiterProvider {

    private static RateLimiterProvider rateLimiterProvider = new RateLimiterProvider();

    private final SlidingWindowLogsRateLimiter slidingWindowLogsRateLimiter;
    private final FixedWindowCountersRateLimiter fixedWindowCountersRateLimiter;

    private RateLimiterProvider() {
        this.slidingWindowLogsRateLimiter = new SlidingWindowLogsRateLimiter();
        this.fixedWindowCountersRateLimiter = new FixedWindowCountersRateLimiter();
    }

    public static RateLimiterProvider getProviderInstance() {
        return rateLimiterProvider;
    }

    public RateLimiter getRateLimiter(RateLimiterTypes type) {
        switch (type) {
            case SLIDING_WINDOW_LOG:
                return slidingWindowLogsRateLimiter;
            case FIXED_COUNTER:
                return fixedWindowCountersRateLimiter;
            default:
                throw new RuntimeException("provided type " + type + " is not found !");
        }
    }
}
