package ru.mfilatov.prayingtimes.telegrambot.counter;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mfilatov.prayingtimes.telegrambot.entities.Counter;

@Service
public class CounterService {
    @Autowired
    private CounterRepository counterRepository;

    @Transactional
    public int increment() {
        Counter counter = counterRepository.findById(1L).orElse(new Counter());
        counter.setValue(counter.getValue() + 1);
        counterRepository.save(counter);
        return counter.getValue();
    }

    @Transactional
    public int decrement() {
        Counter counter = counterRepository.findById(1L).orElse(new Counter());
        counter.setValue(counter.getValue() - 1);
        counterRepository.save(counter);
        return counter.getValue();
    }

    public int getCurrentValue() {
        return counterRepository.findById(1L).orElse(new Counter()).getValue();
    }

    @Transactional
    public void reset() {
        Counter counter = counterRepository.findById(1L).orElse(new Counter());
        counter.setValue(0);
        counterRepository.save(counter);
    }
}
