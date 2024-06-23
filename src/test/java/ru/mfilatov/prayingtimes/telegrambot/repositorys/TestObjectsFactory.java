package ru.mfilatov.prayingtimes.telegrambot.repositorys;

import java.util.UUID;
import org.apache.commons.lang3.RandomUtils;
import ru.mfilatov.prayingtimes.telegrambot.entities.TelegramUser;

public class TestObjectsFactory {
  public TelegramUser createTelegramUser() {
    return new TelegramUser(
        RandomUtils.nextLong(),
        RandomUtils.nextLong(),
        RandomUtils.nextDouble(),
        RandomUtils.nextDouble(),
        RandomUtils.nextInt());
  }
}
