package ru.mfilatov.prayingtimes.telegrambot.counter;

import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {
    private static final int MAX_REQUESTS_PER_USER = 10; // Max requests per user per minute
    private static final long RATE_LIMIT_TIME_WINDOW = 60_000; // 1 minute in milliseconds

    private final CounterService counter;

    public RateLimiterService(CounterService counter) {
        this.counter = counter;
    }

    public boolean isRateLimited(Long chatId) {
        if(counter.getCurrentValue(chatId) < MAX_REQUESTS_PER_USER) {
            counter.increment(chatId);
            return false;
        }

        if(System.currentTimeMillis() - counter.getInitialTime(chatId) > RATE_LIMIT_TIME_WINDOW) {
            counter.reset(chatId);
            return false;
        }

        return true;
    }
}
