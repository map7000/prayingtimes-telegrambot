package ru.mfilatov.prayingtimes.telegrambot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import ru.mfilatov.prayingtimes.telegrambot.entities.TelegramUser;
import ru.mfilatov.prayingtimes.telegrambot.repositories.TelegramUserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Sql({"schema.sql"})
@AutoConfigureTestDatabase
public class TelegramUserRepositoryTest {

    @Autowired
    private TelegramUserRepository userRepository;

    @Test
    public void testSaveUser() {
        TelegramUser user = new TestObjectFactory().createTelegramUser();
        userRepository.save(user);

        TelegramUser savedUser = userRepository.findById(user.getId()).orElse(null);
        assertThat(savedUser).as("Can find user by ObjectId").isNotNull();
        assertThat(savedUser).as("User data saved correctly").isEqualTo(user);
    }
}
