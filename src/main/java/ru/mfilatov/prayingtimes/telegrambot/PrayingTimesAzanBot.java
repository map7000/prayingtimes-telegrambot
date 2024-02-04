/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.mfilatov.prayingtimes.telegrambot.clients.TimeskeeperClient;

@Slf4j
@Component
public class PrayingTimesAzanBot extends TelegramLongPollingBot {

  private final TimeskeeperClient client;

  @Value("${BOT_NAME}")
  String botName;

  @Autowired
  public PrayingTimesAzanBot(@Value("${BOT_TOKEN}") String botToken, TimeskeeperClient client) {
    super(botToken);
    this.client = client;
  }

  @Override
  @SneakyThrows
  public void onUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().isCommand()) {
      log.info(
          "Command request. UserId: {}, Text: {}",
          update.getMessage().getChatId(),
          update.getMessage().getText());

      SendMessage message = new SendMessage();
      message.setChatId(update.getMessage().getChatId().toString());
      message.setText(
          "This bot currently support no commands, but will send you praying times if you send hims your location. Calculation method Spiritual Administration of Muslims of Russia. Source code for times calculation: https://github.com/map7000/timeskeeper.");
      execute(message);
    } else if (update.hasMessage() && update.getMessage().hasLocation()) {
      log.info(
          "Location request. UserId: {}, Latitude: {}, Longitude: {}",
          update.getMessage().getChatId(),
          update.getMessage().getLocation().getLatitude(),
          update.getMessage().getLocation().getLongitude());

      SendMessage message = new SendMessage();
      message.setChatId(update.getMessage().getChatId().toString());
      message.setText(
          client
              .getTimesByCoordinates(
                  update.getMessage().getLocation().getLatitude(),
                  update.getMessage().getLocation().getLongitude(),
                  14)
              .toString());
      execute(message);
    }
  }

  @Override
  public String getBotUsername() {
    return botName;
  }
}
