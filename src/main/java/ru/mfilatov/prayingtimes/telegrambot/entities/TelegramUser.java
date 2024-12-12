package ru.mfilatov.prayingtimes.telegrambot.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.UUID;

@Data
@AllArgsConstructor(onConstructor = @__(@PersistenceCreator))
public class TelegramUser {
    private final @Id Long id;
    private Long chatId;
    private Double latitude;
    private Double longitude;
    private Integer method;
}
