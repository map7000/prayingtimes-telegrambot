/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "counter")
public class Counter {
  @Id private Long chatId;
  private Integer counterValue;
  private Long initialTime;

  public Counter(Long chatId) {
    this.chatId = chatId;
    counterValue = 0;
    initialTime = System.currentTimeMillis();
  }
}
