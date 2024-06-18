package ru.mfilatov.prayingtimes.telegrambot.entities;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public class TelegramUser {
    @Id
    UUID objectId;
    Long chatId;
    Double latitude;
    Double longitude;
    Integer method;
}
