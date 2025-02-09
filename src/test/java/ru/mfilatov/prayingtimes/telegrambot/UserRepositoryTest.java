/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.mfilatov.prayingtimes.telegrambot.entities.User;
import ru.mfilatov.prayingtimes.telegrambot.repositories.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUser() {
        User user = new TestObjectFactory().createTelegramUser();
        userRepository.save(user);

        var savedUser = userRepository.findByTelegramId(user.getTelegramId());
        assertThat(savedUser).as("Can find user by TelegramId").isNotNull();
        assertThat(savedUser).as("User data saved correctly").isEqualTo(user);
    }
}
