package ru.mfilatov.prayingtimes.telegrambot.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mfilatov.prayingtimes.telegrambot.entities.TelegramUser;

import java.util.UUID;

@Repository
public interface TelegramUserRepository extends CrudRepository<TelegramUser, Long> {
    TelegramUser findByChatId(Long chatId);
}