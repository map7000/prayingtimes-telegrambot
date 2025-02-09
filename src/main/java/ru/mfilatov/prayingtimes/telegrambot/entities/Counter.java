package ru.mfilatov.prayingtimes.telegrambot.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Counter {
    @Id
    private Long id = 1L; // Single row for the counter
    @Setter
    @Getter
    private int value;

}