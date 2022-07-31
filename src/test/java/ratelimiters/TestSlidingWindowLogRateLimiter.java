package ratelimiters;

import model.RateLimit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSlidingWindowLogRateLimiter {
    private static ScheduledExecutorService executorService;

    private static SlidingWindowLogsRateLimiter slidingWindowLogsRateLimiter;
    private static final int NUM_THREADS = 5;

    @BeforeAll
    static void setUp() {
        executorService = Executors.newScheduledThreadPool(NUM_THREADS);
        slidingWindowLogsRateLimiter = new SlidingWindowLogsRateLimiter();
    }

    @Test
    public void givenUserIdWithConfiguredRateLimits_whenSlidingWindowRateLimiterIsRequested_thenRateLimitTheRequest()
            throws InterruptedException {
        var configurationThread = new ConfigurationThread("userId", new RateLimit(10, 60));
        executorService.execute(configurationThread);

        List<ScheduledFuture<Boolean>> serviceCalls = new ArrayList<>();

        ServiceCallThread serviceCallThread = new ServiceCallThread("userId");
        serviceCalls.add(executorService.schedule(serviceCallThread, 10, TimeUnit.SECONDS));
        for (int i=0; i<3; i++)
            serviceCalls.add(executorService.schedule(serviceCallThread, 5, TimeUnit.SECONDS));

        for (int i=0; i<9; i++)
            serviceCalls.add(executorService.schedule(serviceCallThread, 5, TimeUnit.SECONDS));


        assertEquals(serviceCalls.size(), 13);

        assertEquals(10, serviceCalls.stream()
                .filter(serviceCall -> {
                    try {
                        return serviceCall.get().equals(Boolean.TRUE);
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }).count());

        assertEquals(3, serviceCalls.stream()
                .filter(serviceCall -> {
                    try {
                        return serviceCall.get().equals(Boolean.FALSE);
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }).count());

        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);
    }

    private static class ConfigurationThread implements Runnable {
        private final RateLimit rateLimit;
        private final String userId;

        public ConfigurationThread(String userId, RateLimit rateLimit) {
            this.userId = userId;
            this.rateLimit = rateLimit;
        }
        @Override
        public void run() {
            System.out.println("Configuration added for user " + userId + " for " + rateLimit.getNumberOfRequests() +
                    " in " + rateLimit.getTimeInSeconds() + " seconds !");
            slidingWindowLogsRateLimiter.addUser(this.userId,
                    rateLimit.getNumberOfRequests(),
                    rateLimit.getTimeInSeconds());
        }
    }

    private static class ServiceCallThread implements Callable<Boolean> {
        private final String userId;

        public ServiceCallThread(String userId) {
            this.userId = userId;
        }
        @Override
        public Boolean call() {
            return slidingWindowLogsRateLimiter.shouldBeRateLimited(this.userId);
        }
    }
}
