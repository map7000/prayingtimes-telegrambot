/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.mfilatov.prayingtimes.telegrambot.clients.TimeskeeperClient;
import ru.mfilatov.prayingtimes.telegrambot.entities.Event;
import ru.mfilatov.prayingtimes.telegrambot.entities.User;
import ru.mfilatov.prayingtimes.telegrambot.repositories.EventRepository;
import ru.mfilatov.prayingtimes.telegrambot.repositories.UserRepository;

@Slf4j
@Component
public class PrayingTimesAzanBot
    implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;

  private String DEFAULT_TEXT = "Source code: https://github.com/map7000/timeskeeper";

  private final TimeskeeperClient client;
  private final EventRepository events;
  private final UserRepository users;
  private final String botToken;

  @Autowired
  public PrayingTimesAzanBot(
      TimeskeeperClient client, EventRepository events, UserRepository users, @Value("${BOT_TOKEN}") String token) {
    this.client = client;
    this.events = events;
    this.users = users;
    this.botToken = token;

    this.telegramClient = new OkHttpTelegramClient(getBotToken());
  }

  @Override
  public String getBotToken() {
    return this.botToken;
  }

  @Override
  public LongPollingUpdateConsumer getUpdatesConsumer() {
    return this;
  }

  @Override
  public void consume(Update update) {
    Long chatId = update.getMessage().getChatId();
    String text = DEFAULT_TEXT;
    Double latitude;
    Double longitude;

    if (update.hasMessage() && update.getMessage().hasLocation()) {
      var eventMessage =
          String.format(
              "Location request. UserId: %s, Latitude: %s, Longitude: %s",
              chatId,
              update.getMessage().getLocation().getLatitude(),
              update.getMessage().getLocation().getLongitude());
      log.info(eventMessage);

      var user = users.findByTelegramId(chatId);
      if (Objects.isNull(user)) {
        user = new User();
        user.setTelegramId(chatId);
      }

      latitude = update.getMessage().getLocation().getLatitude();
      longitude = update.getMessage().getLocation().getLongitude();

      user.setLatitude(latitude);
      user.setLongitude(longitude);

      users.save(user);

      var event = new Event();
      event.setUser(user);
      event.setTimestamp(System.currentTimeMillis());
      event.setDescription(eventMessage);

      events.save(event);

      text = client.getTimesByCoordinates(latitude, longitude, 14).toString();
    }

    SendMessage message = SendMessage.builder().chatId(chatId).text(text).build();

    try {
      telegramClient.execute(message);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }
}
