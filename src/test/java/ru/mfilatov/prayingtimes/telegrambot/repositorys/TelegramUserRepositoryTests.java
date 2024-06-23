package ru.mfilatov.prayingtimes.telegrambot.repositorys;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.mfilatov.prayingtimes.telegrambot.entities.TelegramUser;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Sql({"schema.sql"})
@AutoConfigureTestDatabase
public class TelegramUserRepositoryTests {

    @Autowired
    private TelegramUserRepository userRepository;

    @Test
    public void testSaveUser() {
        TelegramUser user = new TestObjectsFactory().createTelegramUser();
        userRepository.save(user);

        TelegramUser savedUser = userRepository.findById(user.getId()).orElse(null);
        assertThat(savedUser).as("Can find user by ObjectId").isNotNull();
        assertThat(savedUser).as("User data saved correctly").isEqualTo(user);
    }
}
