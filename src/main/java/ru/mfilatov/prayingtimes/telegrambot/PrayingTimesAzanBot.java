/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot;

import static ru.mfilatov.prayingtimes.telegrambot.Constants.*;

import java.util.Objects;
import java.util.regex.Pattern;
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
import ru.mfilatov.prayingtimes.telegrambot.counter.RateLimiterService;
import ru.mfilatov.prayingtimes.telegrambot.entities.User;
import ru.mfilatov.prayingtimes.telegrambot.events.EventHandler;
import ru.mfilatov.prayingtimes.telegrambot.repositories.UserRepository;

@Slf4j
@Component
public class PrayingTimesAzanBot
    implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;
  private final TimeskeeperClient client;
  private final EventHandler events;
  private final UserRepository users;
  private final RateLimiterService rateLimiter;
  private final String botToken;

  private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s.,!?-]+$");

  @Autowired
  public PrayingTimesAzanBot(
      TimeskeeperClient client,
      EventHandler events,
      UserRepository users,
      RateLimiterService rateLimiter,
      @Value("${BOT_TOKEN}") String token) {
    this.client = client;
    this.events = events;
    this.users = users;
    this.rateLimiter = rateLimiter;
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
    Double latitude;
    Double longitude;

    Long chatId = update.getMessage().getChatId();

    if (rateLimiter.isRateLimited(chatId)) {
      sendMessage(chatId, "⚠️ Too many requests. Please try again later.");
      return;
    }

    if (update.hasMessage() && update.getMessage().hasText()) {
      if (!isValidInput(update.getMessage().getText())) {
        sendMessage(
            chatId, "⚠️ Invalid input. Please use only letters, numbers, and common symbols.");
        return;
      }
      var message = update.getMessage();

      switch (message.getText()) {
        case "/start" -> sendStartMenu(chatId);
        case "/help" -> sendHelpMessage(chatId);
        case "/prayertimes" -> sendPrayerTimes(chatId);
        default -> sendDefaultMessage(chatId, message.getText());
      }
    }

    if (update.hasMessage() && update.getMessage().hasLocation()) {
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
      events.newUser(chatId);

      sendMessage(chatId, client.getTimesByCoordinates(latitude, longitude, 14).toString());
    }
  }

  private boolean isValidInput(String input) {
    return ALPHANUMERIC_PATTERN.matcher(input).matches();
  }

  private void sendMessage(Long chatId, String text) {
    SendMessage message = SendMessage.builder().chatId(chatId).text(text).build();
    try {
      telegramClient.execute(message);
    } catch (TelegramApiException e) {
      events.error(chatId, "Failed to send message: " + e.getMessage());
    }
  }

  private void sendHelpMessage(Long chatId) {
    events.help(chatId);
    sendMessage(chatId, HELP_MESSAGE);
  }

  private void sendPrayerTimes(Long chatId) {
    String text = "🕋Please share your location to get prayer times.";
    events.timesRequest(chatId);
    sendMessage(chatId, text);
  }

  private void sendDefaultMessage(Long chatId, String description) {
    events.unknownCommand(chatId, description);
    sendMessage(chatId, DEFAULT_MESSAGE);
  }

  private void sendStartMenu(Long chatId) {
    events.start(chatId);
    sendMessage(chatId, MENU_MESSAGE);
  }
}
