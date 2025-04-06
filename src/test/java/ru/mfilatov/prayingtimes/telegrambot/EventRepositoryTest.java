/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.mfilatov.prayingtimes.telegrambot.entities.Event;
import ru.mfilatov.prayingtimes.telegrambot.repositories.EventRepository;

@DataJpaTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=create-drop"
    })
public class EventRepositoryTest {

  @Autowired private EventRepository eventRepository;

  @Test
  public void testSaveEvent() {

    Event event = new TestObjectFactory().createEvent();
    eventRepository.save(event);

    var savedEvent = eventRepository.findById(event.getId()).orElse(null);
    assertThat(savedEvent).as("Can find event by id").isNotNull();
    assertThat(savedEvent).as("Event data saved correctly").isEqualTo(event);
  }
}
