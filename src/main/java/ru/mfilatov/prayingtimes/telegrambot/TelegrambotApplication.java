/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableJdbcRepositories
@Import(org.telegram.telegrambots.starter.TelegramBotStarterConfiguration.class)
public class TelegrambotApplication {

  public static void main(String[] args) {
    SpringApplication.run(TelegrambotApplication.class, args);
  }
}
