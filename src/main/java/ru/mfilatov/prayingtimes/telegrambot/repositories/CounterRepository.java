/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mfilatov.prayingtimes.telegrambot.entities.Counter;

public interface CounterRepository extends JpaRepository<Counter, Long> {}
