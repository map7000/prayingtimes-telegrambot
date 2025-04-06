/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.mfilatov.prayingtimes.telegrambot.entities.Counter;
import ru.mfilatov.prayingtimes.telegrambot.repositories.CounterRepository;

@DataJpaTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=create-drop"
    })
public class CounterRepositoryTest {
  @Autowired private CounterRepository counterRepository;

  @Test
  void saveTest() {
    counterRepository.save(new Counter(1L));
    assertThat(counterRepository.findById(1L).orElseThrow().getCounterValue()).isEqualTo(0);
  }
}
