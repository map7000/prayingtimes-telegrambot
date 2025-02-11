package ru.mfilatov.prayingtimes.telegrambot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mfilatov.prayingtimes.telegrambot.counter.CounterService;
import ru.mfilatov.prayingtimes.telegrambot.entities.Counter;
import ru.mfilatov.prayingtimes.telegrambot.repositories.CounterRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CounterServiceTest {

    @Mock
    private CounterRepository counterRepository;

    @InjectMocks
    private CounterService counterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIncrement() {
        Long chatId = 123L;
        Counter counter = new Counter(chatId);
        counter.setCounterValue(5);

        when(counterRepository.findById(chatId)).thenReturn(Optional.of(counter));
        when(counterRepository.save(any(Counter.class))).thenReturn(counter);

        counterService.increment(chatId);

        assertThat(counter.getCounterValue()).isEqualTo(6);
        verify(counterRepository, times(1)).findById(chatId);
        verify(counterRepository, times(1)).save(counter);
    }

    @Test
    void testDecrement() {
        Long chatId = 123L;
        Counter counter = new Counter(chatId);
        counter.setCounterValue(5);

        when(counterRepository.findById(chatId)).thenReturn(Optional.of(counter));
        when(counterRepository.save(any(Counter.class))).thenReturn(counter);

        counterService.decrement(chatId);

        int result = counterService.getCurrentValue(chatId);

        assertThat(result).isEqualTo(4);
        verify(counterRepository, times(2)).findById(chatId);
        verify(counterRepository, times(1)).save(counter);
    }

    @Test
    void testGetCurrentValue() {
        Long chatId = 123L;
        Counter counter = new Counter(chatId);
        counter.setCounterValue(10);

        when(counterRepository.findById(chatId)).thenReturn(Optional.of(counter));

        int result = counterService.getCurrentValue(chatId);

        assertThat(result).isEqualTo(10);
        verify(counterRepository, times(1)).findById(chatId);
    }

    @Test
    void testReset() {
        Long chatId = 123L;
        Counter counter = new Counter(chatId);
        counter.setCounterValue(10);

        when(counterRepository.findById(chatId)).thenReturn(Optional.of(counter));
        when(counterRepository.save(any(Counter.class))).thenReturn(counter);

        counterService.reset(chatId);

        assertThat(counter.getCounterValue()).isEqualTo(0);
        verify(counterRepository, times(1)).findById(chatId);
        verify(counterRepository, times(1)).save(counter);
    }

    @Test
    void testIncrementWhenCounterDoesNotExist() {
        Long chatId = 123L;
        Counter newCounter = new Counter(chatId);
        newCounter.setCounterValue(1);

        when(counterRepository.findById(chatId)).thenReturn(Optional.empty());
        when(counterRepository.save(any(Counter.class))).thenReturn(newCounter);

        counterService.increment(chatId);

        assertThat(newCounter.getCounterValue()).isEqualTo(1);
        verify(counterRepository, times(1)).findById(chatId);
        verify(counterRepository, times(1)).save(any(Counter.class));
    }
}