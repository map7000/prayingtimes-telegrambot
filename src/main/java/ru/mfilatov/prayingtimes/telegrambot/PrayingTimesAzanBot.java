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
import ru.mfilatov.prayingtimes.telegrambot.entities.Event;
import ru.mfilatov.prayingtimes.telegrambot.entities.User;
import ru.mfilatov.prayingtimes.telegrambot.repositories.EventRepository;
import ru.mfilatov.prayingtimes.telegrambot.repositories.UserRepository;

import java.util.Objects;

@Slf4j
@Component
public class PrayingTimesAzanBot extends TelegramLongPollingBot {

  private final TimeskeeperClient client;
  private final EventRepository events;
  private final UserRepository users;

  @Value("${BOT_NAME}")
  String botName;

  @Autowired
  public PrayingTimesAzanBot(@Value("${BOT_TOKEN}") String botToken, TimeskeeperClient client, EventRepository events,
                             UserRepository users) {
    super(botToken);
    this.client = client;
    this.events = events;
    this.users = users;
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

      var user = users.findByTelegramId(update.getMessage().getChatId());

      if(Objects.isNull(user)) {
        message.setText("Please send your location first");
      } else if(update.getMessage().getText().equals("info")) {
        message.setText(
            "This bot currently support no commands, but will send you praying times if you send hims your location. Calculation method Spiritual Administration of Muslims of Russia. Source code for times calculation: https://github.com/map7000/timeskeeper.");
      } else {
        message.setText(client
                .getTimesByCoordinates(
                        update.getMessage().getLocation().getLatitude(),
                        update.getMessage().getLocation().getLongitude(),
                        14)
                .toString());
      }

      execute(message);
    } else if (update.hasMessage() && update.getMessage().hasLocation()) {
      var eventMessage = String.format("Location request. UserId: %s, Latitude: %s, Longitude: %s",
              update.getMessage().getChatId(),
              update.getMessage().getLocation().getLatitude(),
              update.getMessage().getLocation().getLongitude());
      log.info(eventMessage);

      var user = users.findByTelegramId(update.getMessage().getChatId());
      if( Objects.isNull(user)) {
        user = new User();
        user.setTelegramId(update.getMessage().getChatId());
      }

      user.setLatitude(update.getMessage().getLocation().getLatitude());
      user.setLongitude(update.getMessage().getLocation().getLongitude());

      users.save(user);

      var event = new Event();
      event.setUser(user);
      event.setTimestamp(System.currentTimeMillis());
      event.setDescription(eventMessage);

      events.save(event);

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
