/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mfilatov.prayingtimes.telegrambot.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  User findByTelegramId(Long telegramId);
}
