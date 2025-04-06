/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot;

import org.apache.commons.lang3.RandomUtils;
import ru.mfilatov.prayingtimes.telegrambot.entities.Event;
import ru.mfilatov.prayingtimes.telegrambot.entities.User;
import ru.mfilatov.prayingtimes.telegrambot.events.EventType;

public class TestObjectFactory {
  public User createTelegramUser() {
    var user = new User();
    user.setTelegramId(RandomUtils.insecure().randomLong());
    user.setLatitude(RandomUtils.insecure().randomDouble());
    user.setLongitude(RandomUtils.insecure().randomDouble());
    return user;
  }

  public Event createEvent() {
    var event = new Event();
    event.setEventType(EventType.START);
    event.setChatId(1L);
    event.setTimestamp(System.currentTimeMillis());
    event.setDescription("Test event");
    return event;
  }
}
