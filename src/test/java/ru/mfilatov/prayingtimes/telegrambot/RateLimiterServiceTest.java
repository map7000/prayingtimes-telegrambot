/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mfilatov.prayingtimes.telegrambot.counter.CounterService;
import ru.mfilatov.prayingtimes.telegrambot.counter.RateLimiterService;

class RateLimiterServiceTest {

  @Mock private CounterService counterService;

  @InjectMocks private RateLimiterService rateLimiterService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testIsRateLimited_WhenBelowLimit_ShouldReturnFalseAndIncrementCounter() {
    Long chatId = 123L;
    when(counterService.getCurrentValue(chatId)).thenReturn(5);
    when(counterService.getInitialTime(chatId)).thenReturn(System.currentTimeMillis());

    boolean result = rateLimiterService.isRateLimited(chatId);

    assertThat(result).isFalse();
    verify(counterService, times(1)).getCurrentValue(chatId);
    verify(counterService, times(1)).increment(chatId);
    verify(counterService, never()).reset(chatId);
  }

  @Test
  void testIsRateLimited_WhenAtLimitButTimeWindowExpired_ShouldReturnFalseAndResetCounter() {
    Long chatId = 123L;
    when(counterService.getCurrentValue(chatId)).thenReturn(10);
    when(counterService.getInitialTime(chatId)).thenReturn(System.currentTimeMillis() - 61_000);

    boolean result = rateLimiterService.isRateLimited(chatId);

    assertThat(result).isFalse();
    verify(counterService, times(1)).getCurrentValue(chatId);
    verify(counterService, times(1)).reset(chatId);
    verify(counterService, never()).increment(chatId);
  }

  @Test
  void testIsRateLimited_WhenAtLimitAndTimeWindowNotExpired_ShouldReturnTrue() {
    Long chatId = 123L;
    when(counterService.getCurrentValue(chatId)).thenReturn(10);
    when(counterService.getInitialTime(chatId)).thenReturn(System.currentTimeMillis() - 30_000);

    boolean result = rateLimiterService.isRateLimited(chatId);

    assertThat(result).isTrue(); // Rate-limited
    verify(counterService, times(1)).getCurrentValue(chatId);
    verify(counterService, never()).reset(chatId);
    verify(counterService, never()).increment(chatId);
  }

  @Test
  void testIsRateLimited_WhenCounterDoesNotExist_ShouldReturnFalseAndIncrementCounter() {
    Long chatId = 123L;
    when(counterService.getCurrentValue(chatId)).thenReturn(0);
    when(counterService.getInitialTime(chatId)).thenReturn(System.currentTimeMillis());

    boolean result = rateLimiterService.isRateLimited(chatId);

    assertThat(result).isFalse();
    verify(counterService, times(1)).getCurrentValue(chatId);
    verify(counterService, times(1)).increment(chatId);
    verify(counterService, never()).reset(chatId);
  }
}
