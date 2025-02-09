package ru.mfilatov.prayingtimes.telegrambot.counter;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mfilatov.prayingtimes.telegrambot.entities.Counter;

public interface CounterRepository extends JpaRepository<Counter, Long> {
}