package ru.mfilatov.prayingtimes.telegrambot.counter;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mfilatov.prayingtimes.telegrambot.entities.Counter;
import ru.mfilatov.prayingtimes.telegrambot.repositories.CounterRepository;

@Service
public class CounterService {
    private final CounterRepository counterRepository;

    @Autowired
    CounterService(CounterRepository counterRepository) {
        this.counterRepository = counterRepository;
    }

    @Transactional
    public void increment(Long chatId) {
        Counter counter = counterRepository.findById(chatId).orElse(new Counter(chatId));
        counter.setCounterValue(counter.getCounterValue() + 1);
        counterRepository.save(counter);
    }

    @Transactional
    public void decrement(Long chatId) {
        Counter counter = counterRepository.findById(chatId).orElse(new Counter(chatId));
        counter.setCounterValue(counter.getCounterValue() - 1);
        counterRepository.save(counter);
    }

    public int getCurrentValue(Long chatId) {
        return counterRepository.findById(chatId).orElse(new Counter(chatId)).getCounterValue();
    }

    public Long getInitialTime(Long chatId) {
        return counterRepository.findById(chatId).orElse(new Counter(chatId)).getInitialTime();
    }

    @Transactional
    public void reset(Long chatId) {
        Counter counter = counterRepository.findById(chatId).orElse(new Counter(chatId));
        counter.setCounterValue(0);
        counter.setInitialTime(System.currentTimeMillis());
        counterRepository.save(counter);
    }
}
